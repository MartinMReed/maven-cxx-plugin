/**
 * Copyright (c) 2010-2013 Martin M Reed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hardisonbrewing.maven.cxx.xcode;

import generated.xcode.BuildAction;
import generated.xcode.BuildActionEntries;
import generated.xcode.BuildActionEntries.BuildActionEntry;
import generated.xcode.BuildableReference;
import generated.xcode.Scheme;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @goal xcode-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            executeScheme( scheme );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                List<String> cmd = buildCommand( target, false );
                Properties buildSettings = loadBuildSettings( cmd );
                PropertiesService.storeBuildSettings( buildSettings, target );
                execute( cmd );
            }
        }
    }

    private void executeScheme( String scheme ) {

        File schemeFile = XCodeService.findXcscheme( scheme );
        boolean expectedScheme = XCodeService.isExpectedScheme( scheme, schemeFile.getPath() );
        File schemeTmpFile = null;

//        getLog().info( "schemeFile[" + schemeFile.getPath() + "], expectedScheme[" + expectedScheme + "]" );

        try {

            if ( expectedScheme ) {
                File targetDirectory = TargetDirectoryService.getTargetDirectory();
                schemeTmpFile = new File( targetDirectory, schemeFile.getName() );
                FileUtils.copyFile( schemeFile, schemeTmpFile );
            }
            else {

                schemeTmpFile = new File( XCodeService.getSchemeDirPath() );
                schemeTmpFile.mkdir();

                File userFile = schemeFile;

                schemeFile = new File( XCodeService.getSchemePath( scheme ) );
                FileUtils.ensureParentExists( schemeFile.getPath() );

                FileUtils.copyFile( userFile, schemeFile );
            }

            List<String> cmd = buildCommand( scheme, true );
            Properties buildSettings = loadBuildSettings( cmd );
            PropertiesService.storeBuildSettings( buildSettings, scheme );

            injectPostAction( scheme, schemeFile, buildSettings );

            execute( cmd );
        }
        catch (Exception e) {
            getLog().error( "Unable to inject Scheme with PostAction" );
            throw new IllegalStateException( e );
        }
        finally {

            try {
                if ( expectedScheme ) {
                    FileUtils.copyFile( schemeTmpFile, schemeFile );
                }
                else {
                    FileUtils.deleteDirectory( schemeTmpFile );
                }
            }
            catch (IOException e) {
                getLog().error( "Unable to cleanup injected Scheme resource: " + schemeTmpFile );
                throw new IllegalStateException( e );
            }
        }
    }

    private List<String> buildCommand( String target, boolean scheme ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcodebuild" );

        String workspacePath = XCodeService.getXcworkspacePath();

        if ( workspacePath != null ) {
            cmd.add( "-workspace" );
            cmd.add( workspacePath );
        }
        else {
            cmd.add( "-project" );
            cmd.add( XCodeService.getXcprojPath() );
        }

        if ( scheme ) {
            cmd.add( "-scheme" );
        }
        else {
            cmd.add( "-target" );
        }
        cmd.add( target );

        String configuration = XCodeService.getConfiguration( target );
        cmd.add( "-configuration" );
        cmd.add( configuration );

        if ( scheme ) {
            cmd.add( XCodeService.ACTION_ARCHIVE );
        }
        else {

            cmd.add( XCodeService.ACTION_BUILD );

            StringBuffer symroot = new StringBuffer();
            symroot.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
            symroot.append( File.separator );
            symroot.append( "Build" );
            cmd.add( "SYMROOT=" + symroot.toString() );

            cmd.add( "BUILD_DIR=$(SYMROOT)" );
            cmd.add( "CONFIGURATION_BUILD_DIR=$(BUILD_DIR)" + File.separator + "Products" );
            cmd.add( "OBJROOT=$(BUILD_DIR)" + File.separator + "Intermediates" );
        }

        cmd.add( "CONFIGURATION=" + configuration );

        String codeSignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codeSignIdentity != null ) {
            cmd.add( "CODE_SIGN_IDENTITY=" + codeSignIdentity );
        }
        else {
            getLog().info( "No codesign identity found. Disabling signing..." );
            cmd.add( "CODE_SIGN_IDENTITY=" );
            cmd.add( "CODE_SIGNING_REQUIRED=NO" );
        }

        return cmd;
    }

    @Override
    protected Commandline buildCommandline( List<String> cmd ) {

        try {
            return CommandLineService.build( cmd );
        }
        catch (CommandLineException e) {
            throw new IllegalStateException( e );
        }
    }

    private Properties loadBuildSettings( List<String> cmd ) {

        cmd = new LinkedList<String>( cmd );
        cmd.add( "-showBuildSettings" );

        Properties properties = new Properties();
        PropertyStreamConsumer streamConsumer = new PropertyStreamConsumer( properties );
        execute( cmd, streamConsumer, null );

        return properties;
    }

    private void injectPostAction( String target, File file, Properties buildSettings ) throws Exception {

        Document document = loadSchemeDocument( file );
        Element root = (Element) document.getFirstChild();

        NodeList archiveActions = root.getElementsByTagName( "ArchiveAction" );
        Element archiveAction = (Element) archiveActions.item( 0 );

        Element postActions;

        NodeList _postActions = archiveAction.getElementsByTagName( "PostActions" );
        if ( _postActions.getLength() > 0 ) {
            postActions = (Element) _postActions.item( 0 );
        }
        else {
            postActions = document.createElement( "PostActions" );
            archiveAction.appendChild( postActions );
        }

        String script = "env > " + PropertiesService.getBuildEnvironmentPropertiesPath( target );

        Element postAction = createPostAction( document, script, file, buildSettings );
        postActions.appendChild( postAction );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform( new DOMSource( document ), new StreamResult( file ) );
    }

    private Document loadSchemeDocument( File file ) throws ParserConfigurationException {

        FullScheme fullScheme = unmarshal( file, FullScheme.class );

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement( "Scheme" );
        document.appendChild( root );

        for (Object object : fullScheme.any) {
            Element element = (Element) object;
            Node node = document.adoptNode( element );
            root.appendChild( node );
        }

        for (Map.Entry<QName, String> attribute : fullScheme.attributes.entrySet()) {
            root.setAttribute( attribute.getKey().toString(), attribute.getValue() );
        }

        return document;
    }

    private Element createPostAction( Document document, String script, File file, Properties buildSettings ) throws Exception {

        Element executionAction = document.createElement( "ExecutionAction" );
        executionAction.setAttribute( "ActionType", "Xcode.IDEStandardExecutionActionsCore.ExecutionActionType.ShellScriptAction" );

        Element actionContent = document.createElement( "ActionContent" );
        actionContent.setAttribute( "title", "Run Script" );
        actionContent.setAttribute( "scriptText", script );
        executionAction.appendChild( actionContent );

        Scheme scheme = unmarshal( file, Scheme.class );
        String target = (String) buildSettings.get( "TARGET_NAME" );
        BuildableReference buildableReference = getBuildableReference( scheme, target );

        Element environmentBuildable = document.createElement( "EnvironmentBuildable" );
        actionContent.appendChild( environmentBuildable );

        Element _buildableReference = createBuildableReference( document, buildableReference );
        environmentBuildable.appendChild( _buildableReference );

        return executionAction;
    }

    private Element createBuildableReference( Document document, BuildableReference buildableReference ) {

        Element element = document.createElement( "BuildableReference" );
        element.setAttribute( "BuildableIdentifier", buildableReference.getBuildableIdentifier() );
        element.setAttribute( "BlueprintIdentifier", buildableReference.getBlueprintIdentifier() );
        element.setAttribute( "BuildableName", buildableReference.getBuildableName() );
        element.setAttribute( "BlueprintName", buildableReference.getBlueprintName() );
        element.setAttribute( "ReferencedContainer", buildableReference.getReferencedContainer() );
        return element;
    }

    private BuildableReference getBuildableReference( Scheme scheme, String target ) {

        BuildAction buildAction = scheme.getBuildAction();
        BuildActionEntries _buildActionEntries = buildAction.getBuildActionEntries();
        List<BuildActionEntry> buildActionEntries = _buildActionEntries.getBuildActionEntry();

        for (int i = 0; i < buildActionEntries.size(); i++) {
            BuildActionEntry buildActionEntry = buildActionEntries.get( i );
            BuildableReference buildableReference = buildActionEntry.getBuildableReference();
            if ( target.equals( buildableReference.getBlueprintName() ) ) {
                return buildableReference;
            }
        }

        getLog().error( "Unable to locate BuildableReference for target: " + target );
        throw new IllegalStateException();
    }

    private <T> T unmarshal( File file, Class<T> clazz ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate Xcscheme file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, clazz );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal Xcscheme file: " + file );
            throw new IllegalStateException( e );
        }
    }

    @XmlAccessorType( XmlAccessType.FIELD )
    @XmlType( name = "", propOrder = { "any" } )
    @XmlRootElement( name = "Scheme" )
    public static class FullScheme {

        @XmlAnyElement( lax = true )
        public List<Object> any;

        @XmlAnyAttribute
        public final Map<QName, String> attributes = new HashMap<QName, String>();
    }

    private static class PropertyStreamConsumer implements StreamConsumer {

        private final Properties properties;

        public PropertyStreamConsumer(Properties properties) {

            this.properties = properties;
        }

        @Override
        public void consumeLine( String line ) {

            line = line.trim();

            int indexOf = line.indexOf( '=' );
            if ( indexOf == -1 ) {
                return;
            }

            String key = line.substring( 0, indexOf ).trim();
            String value = line.substring( indexOf + 1 ).trim();
            properties.put( key, value );
        }
    }
}
