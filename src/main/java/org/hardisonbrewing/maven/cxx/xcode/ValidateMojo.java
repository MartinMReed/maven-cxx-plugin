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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.PropertiesService;

/**
 * @goal xcode-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String[] targetIncludes;

    /**
     * @parameter
     */
    public String[] targetExcludes;

    /**
     * @parameter
     */
    public String scheme;

    /**
     * @parameter
     */
    public Keychain keychain;

    /**
     * @parameter
     */
    public String provisioningProfile;

    /**
     * @parameter
     */
    public String configuration;

    /**
     * @parameter
     */
    public String codesignCertificate;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        validateOS();

        File workspace = XCodeService.loadWorkspace();
        File project = null;

        if ( workspace != null ) {
            validateWorkspace();
        }
        else {
            project = XCodeService.loadProject();
            validateProject( project );
        }

        if ( scheme != null ) {
            try {
                if ( workspace != null ) {
                    validateWorkspaceScheme( scheme, workspace.getName() );
                }
                else {
                    validateProjectScheme( scheme, project.getName() );
                }
            }
            catch (CommandLineException exception) {
                throw new MojoExecutionException( "", exception );
            }
        }

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "provisioningProfile", provisioningProfile, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "configuration", configuration, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "codesignCertificate", codesignCertificate, false );

        String keychainPassword = keychain == null ? null : keychain.password;
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "keychain", keychain, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "<keychain><password/></keychain>", keychainPassword, keychain != null );
    }

    private void validateOS() {

        Properties properties = PropertiesService.getProperties();
        String osName = properties.getProperty( "os.name" );
        if ( !"Mac OS X".equals( osName ) ) {
            getLog().error( "Unsupported OS: " + osName );
            throw new IllegalStateException();
        }
    }

    private void validateProject( File project ) {

        if ( project == null ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate project entry! Expected a file with the extension `" + XCodeService.XCODEPROJ_EXTENSION + "`." );
            throw new IllegalStateException();
        }

        if ( targetIncludes != null && targetExcludes != null ) {
            if ( targetIncludes.length > 0 && targetExcludes.length > 0 ) {
                JoJoMojo.getMojo().getLog().error( "Invalid target configuration! The pom.xml must not specify both `targetIncludes` and `targetExcludes`." );
                throw new IllegalStateException();
            }
        }
    }

    private void validateWorkspace() {

        if ( targetIncludes != null || targetExcludes != null ) {
            if ( targetIncludes.length > 0 || targetExcludes.length > 0 ) {
                JoJoMojo.getMojo().getLog().error( "Invalid workspace configuration! The pom.xml must not specify any targets in `targetIncludes` or `targetExcludes`." );
                throw new IllegalStateException();
            }
        }
    }

    private void validateWorkspaceScheme( String scheme, String workspace ) throws CommandLineException {

        getLog().debug( "Validating Scheme " + scheme );

        if ( schemeExistsInWorkspace( workspace, scheme ) ) {
            return;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Invalid scheme! `" );
        stringBuffer.append( scheme );
        stringBuffer.append( "` was not found. Available options are:" );
        stringBuffer.append( listOfWorkspaceSchemes( workspace, scheme ) );
        getLog().error( stringBuffer.toString() );
        throw new IllegalStateException();
    }

    private void validateProjectScheme( String scheme, String project ) throws CommandLineException {

        getLog().debug( "Validating Scheme " + scheme );

        if ( schemeExistsInProject( project, scheme ) ) {
            return;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Invalid scheme! `" );
        stringBuffer.append( scheme );
        stringBuffer.append( "` was not found. Available options are:" );
        stringBuffer.append( listOfProjectSchemes( project, scheme ) );
        getLog().error( stringBuffer.toString() );
        throw new IllegalStateException();
    }

    private boolean schemeExistsInProject( String project, String scheme ) throws CommandLineException {

        return listOfProjectSchemes( project, scheme ).contains( scheme + "\n" );
    }

    private boolean schemeExistsInWorkspace( String workspace, String scheme ) throws CommandLineException {

        return listOfWorkspaceSchemes( workspace, scheme ).contains( scheme + "\n" );
    }

    /**
     * Run xcodebuild -list -project project to find list of schemes in a project.
     * 
     * @param project
     * @param scheme
     * @return Printed out list of Schemes for the given Project
     * @throws CommandLineException
     */
    private String listOfProjectSchemes( String project, String scheme ) throws CommandLineException {

        return listOfSchemes( "-project", project );
    }

    /**
     * Run xcodebuild -list -workspace workspace to find list of schemes in a workspace.
     * 
     * @param workspace
     * @param scheme
     * @return Printed out list of Schemes for the given Workspace
     * @throws CommandLineException
     */
    private String listOfWorkspaceSchemes( String workspace, String scheme ) throws CommandLineException {

        return listOfSchemes( "-workspace", workspace );
    }

    private String listOfSchemes( String option, String value ) throws CommandLineException {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcodebuild" );
        cmd.add( "-list" );

        cmd.add( option );
        cmd.add( value );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        CommandLineService.execute( cmd, streamConsumer, null );

        String schemesHeader = "Schemes:";

        String listOfSchemes = streamConsumer.getOutput().trim();
        int schemesIndex = listOfSchemes.indexOf( schemesHeader );
        listOfSchemes = listOfSchemes.substring( schemesIndex + schemesHeader.length() );

        int doubleNewlineIndex = listOfSchemes.indexOf( "\n\n" );
        if ( doubleNewlineIndex > -1 ) {
            listOfSchemes = listOfSchemes.substring( 0, doubleNewlineIndex );
        }
        return listOfSchemes;
    }
}
