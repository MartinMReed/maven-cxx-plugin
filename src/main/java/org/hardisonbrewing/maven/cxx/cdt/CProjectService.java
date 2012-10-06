/**
 * Copyright (c) 2011-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.cdt;

import generated.net.rim.bar.Asset;
import generated.net.rim.bar.BarDescriptor;
import generated.org.eclipse.cdt.Cproject;
import generated.org.eclipse.cdt.Entry;
import generated.org.eclipse.cdt.ListOptionValue;
import generated.org.eclipse.cdt.Option;
import generated.org.eclipse.cdt.StorageModule;
import generated.org.eclipse.cdt.StorageModule.Cconfiguration;
import generated.org.eclipse.cdt.StorageModule.Configuration;
import generated.org.eclipse.cdt.StorageModule.Configuration.FolderInfo;
import generated.org.eclipse.cdt.StorageModule.Configuration.SourceEntries;
import generated.org.eclipse.cdt.StorageModule.Project;
import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Builder;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.qnx.BarDescriptorService;

public class CProjectService {

    public static final String CPROJECT_FILENAME = ".cproject";

    public static final String MODULE_SETTINGS = "org.eclipse.cdt.core.settings";

    public static final String BUILDER_DEFAULT = "org.eclipse.cdt.build.core.settings.default.builder";

    private static final String CDT_BUILD_SYSTEM = "cdtBuildSystem";

    private static final String BUILD_TYPE = "org.eclipse.cdt.build.core.buildType";
    private static final String BUILD_TYPE_RELEASE = BUILD_TYPE + ".release";
    private static final String BUILD_TYPE_DEBUG = BUILD_TYPE + ".debug";

    private static final String BUILD_ARTIFACT_TYPE = "org.eclipse.cdt.build.core.buildArtefactType";
    private static final String BUILD_ARTIFACT_TYPE_EXE = BUILD_ARTIFACT_TYPE + ".exe";
    private static final String BUILD_ARTIFACT_TYPE_STATIC_LIB = BUILD_ARTIFACT_TYPE + ".staticLib";

    private static final String SOURCE_KIND_PATH = "sourcePath";

    private static final String VALUE_WORKSPACE_PATH = "VALUE_WORKSPACE_PATH";
    private static final String RESOLVED = "RESOLVED";

    private static Cproject cproject;

    public static File getCProjectFile() {

        return new File( getCProjectFilePath() );
    }

    public static String getCProjectFilePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( CPROJECT_FILENAME );
        return stringBuffer.toString();
    }

    public static String getProjectName() {

        return getProjectName( getCProject() );
    }

    public static String getBuildPath( Configuration configuration, Builder builder ) {

        String buildPath = builder.getBuildPath();

        if ( PropertiesService.isWorkspaceValue( buildPath ) ) {
            buildPath = PropertiesService.getWorkspacePath( configuration, buildPath );
        }

        return buildPath;
    }

    public static String getBuildFilePath( Configuration configuration ) {

        ToolChain toolChain = getToolChain( configuration );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getBuildPath( configuration, toolChain.getBuilder() ) );
        stringBuffer.append( File.separator );

        if ( isStaticLib( configuration ) ) {
            stringBuffer.append( "lib" );
            stringBuffer.append( getProjectName() );
            stringBuffer.append( "." );
            stringBuffer.append( configuration.getArtifactExtension() );
        }
        else if ( QccToolChain.matches( configuration ) ) {
            BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();
            Asset entryPoint = BarDescriptorService.getEntryPoint( barDescriptor, configuration.getName() );
            stringBuffer.append( entryPoint.getValue() );
        }
        else {
            stringBuffer.append( getProjectName() );
            stringBuffer.append( "." );
            stringBuffer.append( configuration.getArtifactExtension() );
        }

        return stringBuffer.toString();
    }

    public static Configuration[] getBuildConfigurations() {

        return getBuildConfigurations( getCProject(), MODULE_SETTINGS );
    }

    public static Configuration getBuildConfiguration( String name ) {

        return getBuildConfiguration( getCProject(), MODULE_SETTINGS, name );
    }

    public static boolean isApplication( Configuration configuration ) {

        return BUILD_ARTIFACT_TYPE_EXE.equals( configuration.getBuildArtefactType() );
    }

    public static boolean isStaticLib( Configuration configuration ) {

        return BUILD_ARTIFACT_TYPE_STATIC_LIB.equals( configuration.getBuildArtefactType() );
    }

    public static boolean isMakefileBuilder( Builder builder ) {

        Boolean managedBuildOn = builder.isManagedBuildOn();
        return ( managedBuildOn == null || !managedBuildOn ) && BUILDER_DEFAULT.equals( builder.getSuperClass() );
    }

    private static List<Cconfiguration> getCconfigurations( Cproject cproject, String module ) {

        for (StorageModule storageModule : cproject.getStorageModule()) {
            if ( module.equals( storageModule.getModuleId() ) ) {
                return storageModule.getCconfiguration();
            }
        }

        return null;
    }

    public static String getProjectName( Cproject cproject ) {

        Project project = getProject( cproject );
        if ( project == null ) {
            return null;
        }
        return project.getName();
    }

    private static Project getProject( Cproject cproject ) {

        StorageModule storageModule = getStorageModule( cproject, CDT_BUILD_SYSTEM );
        if ( storageModule == null ) {
            return null;
        }
        return storageModule.getProject();
    }

    private static StorageModule getStorageModule( Cproject cproject, String id ) {

        for (StorageModule storageModule : cproject.getStorageModule()) {
            if ( id.equals( storageModule.getModuleId() ) ) {
                return storageModule;
            }
        }

        return null;
    }

    private static Configuration getBuildConfiguration( Cconfiguration cconfiguration ) {

        return getConfiguration( cconfiguration, CDT_BUILD_SYSTEM );
    }

    private static Configuration getConfiguration( Cconfiguration cconfiguration, String id ) {

        for (StorageModule storageModule : cconfiguration.getStorageModule()) {
            if ( id.equals( storageModule.getModuleId() ) ) {
                return storageModule.getConfiguration();
            }
        }

        return null;
    }

    public static String[] getSourcePaths( Configuration configuration ) {

        SourceEntries sourceEntries = configuration.getSourceEntries();
        if ( sourceEntries == null ) {
            return null;
        }

        List<Entry> entries = sourceEntries.getEntry();
        if ( entries == null || entries.isEmpty() ) {
            return null;
        }

        List<String> sourcePaths = new ArrayList<String>();

        for (Entry entry : entries) {
            if ( SOURCE_KIND_PATH.equals( entry.getKind() ) ) {
                sourcePaths.add( getSourcePath( entry ) );
            }
        }

        String[] _sourcePaths = new String[sourcePaths.size()];
        sourcePaths.toArray( _sourcePaths );
        return _sourcePaths;
    }

    private static String getSourcePath( Entry entry ) {

        if ( !SOURCE_KIND_PATH.equals( entry.getKind() ) ) {
            return null;
        }

        String[] flags = getFlags( entry );
        Arrays.sort( flags, 0, flags.length );

        StringBuffer stringBuffer = new StringBuffer();

        if ( Arrays.binarySearch( flags, VALUE_WORKSPACE_PATH ) >= 0 ) {
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
        }

        stringBuffer.append( entry.getName() );
        return stringBuffer.toString();
    }

    private static String[] getFlags( Entry entry ) {

        String flags = entry.getFlags();
        if ( flags == null || flags.length() == 0 ) {
            return null;
        }
        return flags.split( "\\|" );
    }

    public static ToolChain getToolChain( Configuration configuration ) {

        List<FolderInfo> folderInfos = configuration.getFolderInfo();
        FolderInfo folderInfo = folderInfos.get( 0 );
        List<ToolChain> toolChains = folderInfo.getToolChain();
        return toolChains.get( 0 );
    }

    public static Tool getTool( ToolChain toolChain, String superClass ) {

        for (Tool tool : toolChain.getTool()) {
            if ( superClass.equals( tool.getSuperClass() ) ) {
                return tool;
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Unsupported tool[" );
        stringBuffer.append( superClass );
        stringBuffer.append( "]. Available options are:" );
        for (Tool tool : toolChain.getTool()) {
            stringBuffer.append( "\n  " );
            stringBuffer.append( tool.getSuperClass() );
        }
        JoJoMojo.getMojo().getLog().error( stringBuffer.toString() );
        throw new UnsupportedOperationException();
    }

    private static Option getToolOption( Tool tool, String superClass ) {

        for (Option option : tool.getOption()) {
            if ( superClass.equals( option.getSuperClass() ) ) {
                return option;
            }
        }
        return null;
    }

    public static String getToolOptionValue( Tool tool, String superClass ) {

        Option option = getToolOption( tool, superClass );
        if ( option == null ) {
            return null;
        }
        return option.getValue();
    }

    public static String[] getToolOptionValues( Tool tool, String superClass ) {

        Option option = getToolOption( tool, superClass );
        if ( option == null ) {
            return null;
        }
        return getOptionValues( option );
    }

    private static Option getToolChainOption( ToolChain toolChain, String superClass ) {

        for (Option option : toolChain.getOption()) {
            if ( superClass.equals( option.getSuperClass() ) ) {
                return option;
            }
        }
        return null;
    }

    public static String getToolChainOptionValue( ToolChain toolChain, String superClass ) {

        Option option = getToolChainOption( toolChain, superClass );
        if ( option == null ) {
            return null;
        }
        return option.getValue();
    }

    private static String[] getOptionValues( Option option ) {

        List<ListOptionValue> listOptionValues = option.getListOptionValue();
        String[] values = new String[listOptionValues.size()];

        for (int i = 0; i < values.length; i++) {
            ListOptionValue listOptionValue = listOptionValues.get( i );
            values[i] = listOptionValue.getValue();
        }

        return values;
    }

    private static Configuration[] getBuildConfigurations( Cproject cproject, String module ) {

        List<Configuration> configurations = new ArrayList<Configuration>();

        for (Cconfiguration cconfiguration : getCconfigurations( cproject, module )) {
            Configuration configuration = getBuildConfiguration( cconfiguration );
            configurations.add( configuration );
        }

        Configuration[] _configurations = new Configuration[configurations.size()];
        configurations.toArray( _configurations );
        return _configurations;
    }

    private static Configuration getBuildConfiguration( Cproject cproject, String module, String name ) {

        for (Cconfiguration cconfiguration : getCconfigurations( cproject, module )) {
            Configuration configuration = getBuildConfiguration( cconfiguration );
            if ( name.equals( configuration.getName() ) ) {
                return configuration;
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Unsupported configuration[" );
        stringBuffer.append( name );
        stringBuffer.append( "]. Available options are:" );
        for (Cconfiguration cconfiguration : getCconfigurations( cproject, module )) {
            Configuration configuration = getBuildConfiguration( cconfiguration );
            stringBuffer.append( "\n  " );
            stringBuffer.append( configuration.getName() );
        }
        JoJoMojo.getMojo().getLog().error( stringBuffer.toString() );
        throw new UnsupportedOperationException();
    }

    public static Properties getBuildProperties( Configuration configuration ) {

        String buildProperties = configuration.getBuildProperties();

        Properties properties = new Properties();
        for (String entry : buildProperties.split( "," )) {
            String[] property = entry.split( "=" );
            properties.put( property[0], property[1] );
        }
        return properties;
    }

    public static Cproject getCProject() {

        return cproject;
    }

    public static void loadCProject() {

        File cprojectFile = getCProjectFile();

        if ( !cprojectFile.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate .cproject file: " + cproject );
            throw new IllegalStateException();
        }

        cproject = readCProject( cprojectFile );
    }

    public static Cproject readCProject( File file ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate CPROJECT file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, Cproject.class );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal CPROJECT file: " + file );
            throw new IllegalStateException( e );
        }
    }
}
