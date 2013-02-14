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

import generated.plist.Plist;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.ProjectService;

public final class XCodeService {

    public static final String MOBILEPROVISION_EXTENSION = "mobileprovision";
    public static final String XCSCHEME_EXTENSION = "xcscheme";
    public static final String XCODEPROJ_EXTENSION = "xcodeproj";
    public static final String XCWORKSPACE_EXTENSION = "xcworkspace";
    public static final String IPA_EXTENSION = "ipa";

    public static final String PRODUCT_TYPE_APPLICATION = "com.apple.product-type.application";

    public static final String CODE_SIGN_IDENTITY = "codesignIdentity";

    public static final String PROP_PRODUCT_TYPE = "productType";
    public static final String PROP_DEFAULT_CONFIG_NAME = "defaultConfigurationName";
    public static final String PROP_BUILD_CONFIG_LIST = "buildConfigurationList";
    public static final String PROP_PRODUCT_REFERENCE = "productReference";
    public static final String PROP_TARGETS = "targets";
    public static final String PROP_SCHEME = "scheme";

    public static final String BUILD_INFOPLIST_PATH = "INFOPLIST_PATH";
    public static final String BUILD_BUILT_PRODUCTS_DIR = "BUILT_PRODUCTS_DIR";
    public static final String BUILD_ARCHIVE_PRODUCTS_PATH = "ARCHIVE_PRODUCTS_PATH";
    public static final String BUILD_ARCHIVE_PATH = "ARCHIVE_PATH";
    public static final String BUILD_TARGET_NAME = "TARGET_NAME";
    public static final String BUILD_ACTION = "ACTION";
    public static final String BUILD_FULL_PRODUCT_NAME = "FULL_PRODUCT_NAME";
    public static final String BUILD_EMBEDDED_PROFILE_NAME = "EMBEDDED_PROFILE_NAME";

    public static final String ACTION_BUILD = "build";
    public static final String ACTION_ARCHIVE = "archive";

    public static final String TRUE_YES = "YES";

    private static String project;
    private static String projectPath;

    private static String workspace;
    private static String workspacePath;

    private static String[] schemes;
    private static String scheme;

    private static String configuration;

    private static Hashtable<String, String> fileIndex;

    private static String[] targets;

    private static String keychainPath;

    private XCodeService() {

        // do nothing
    }

    public static String getProductType( String target ) {

        return PropertiesService.getXCodeProperty( target, PROP_PRODUCT_TYPE );
    }

    public static boolean isApplicationType( String target ) {

        return PRODUCT_TYPE_APPLICATION.equals( getProductType( target ) );
    }

    public static final File loadWorkspace() {

        File baseDir = ProjectService.getBaseDir();

        for (File file : baseDir.listFiles()) {
            if ( file.getName().endsWith( XCWORKSPACE_EXTENSION ) ) {
                return file;
            }
        }

        return null;
    }

    public static final String getSchemeXcprojPath( String scheme ) {

        File schemeFile = findXcscheme( scheme );
        if ( schemeFile == null ) {
            return null;
        }

        String schemePath = schemeFile.getPath();
        int lastIndexOf = schemePath.lastIndexOf( XCODEPROJ_EXTENSION );
        schemePath = schemePath.substring( 0, lastIndexOf + XCODEPROJ_EXTENSION.length() );
        return schemePath;
    }

    public static final File findXcscheme( String scheme ) {

        // shared
        File[] files = listSchemes( scheme, null );
        if ( files != null && files.length > 0 ) {
            return files[0];
        }

        Properties properties = PropertiesService.getProperties();
        String username = properties.getProperty( "user.name" );

        // specific user
        files = listSchemes( scheme, username );
        if ( files != null && files.length > 0 ) {
            return files[0];
        }

        // any user
        files = listSchemes( scheme, "*" );
        if ( files == null || files.length == 0 ) {
            return null;
        }

        File latest = null;
        long latestModified = 0;

        for (File file : files) {
            long lastModified = file.lastModified();
            if ( latest == null || lastModified > latestModified ) {
                latestModified = lastModified;
                latest = file;
            }
        }

        return latest;
    }

    public static final void loadSchemes() {

        List<String> schemes = new LinkedList<String>();

        for (File scheme : listSchemes()) {

            String filename = scheme.getName();
            filename = filename.substring( 0, filename.lastIndexOf( XCSCHEME_EXTENSION ) - 1 );

            if ( !schemes.contains( filename ) ) {
                schemes.add( filename );
            }
        }

        String[] _schemes = new String[schemes.size()];
        schemes.toArray( _schemes );
        XCodeService.schemes = _schemes;
    }

    public static boolean isExpectedScheme( String scheme, String filePath ) {

        String schemePath = getSchemePath( scheme, true );
        if ( filePath.matches( schemePath ) ) {
            return true;
        }

        schemePath = getSchemePath( scheme, false );
        if ( filePath.matches( schemePath ) ) {
            return true;
        }

        return false;
    }

    public static File[] listSchemes() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "**" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "xcschemes" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "*." );
        stringBuffer.append( XCSCHEME_EXTENSION );

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { stringBuffer.toString() };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
    }

    private static File[] listSchemes( String scheme, String username ) {

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { getSchemeIncludePath( scheme, username ) };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
    }

    private static String getSchemeIncludePath( String scheme, String username ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "**" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "*" );
        stringBuffer.append( File.separator );
        if ( username == null || username.length() == 0 ) {
            stringBuffer.append( "xcshareddata" );
        }
        else {
            stringBuffer.append( "xcuserdata" );
            stringBuffer.append( File.separator );
            stringBuffer.append( username );
            stringBuffer.append( ".xcuserdatad" );
        }
        stringBuffer.append( File.separator );
        stringBuffer.append( "xcschemes" );
        stringBuffer.append( File.separator );
        stringBuffer.append( scheme );
        stringBuffer.append( "." );
        stringBuffer.append( XCSCHEME_EXTENSION );
        return stringBuffer.toString();
    }

    public static String getSharedDataDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getXcprojPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "xcshareddata" );
        return stringBuffer.toString();
    }

    public static String getUserDataDirPath() {

        Properties properties = PropertiesService.getProperties();
        String username = properties.getProperty( "user.name" );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getXcprojPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "xcuserdata" );
        stringBuffer.append( File.separator );
        stringBuffer.append( username );
        stringBuffer.append( ".xcuserdatad" );
        return stringBuffer.toString();
    }

    public static String getSchemePath( String scheme, boolean shared ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( shared ? getSharedDataDirPath() : getUserDataDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "xcschemes" );
        stringBuffer.append( File.separator );
        stringBuffer.append( scheme );
        stringBuffer.append( "." );
        stringBuffer.append( XCSCHEME_EXTENSION );
        return stringBuffer.toString();
    }

    public static final File loadProject() {

        File baseDir = ProjectService.getBaseDir();

        for (File file : baseDir.listFiles()) {
            if ( file.getName().endsWith( XCODEPROJ_EXTENSION ) ) {
                return file;
            }
        }

        return null;
    }

    public static final String getPbxprojPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getXcprojPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "project.pbxproj" );
        return stringBuffer.toString();
    }

    public static final String getWorkspacedataPath() {

        String workspacePath = getXcworkspacePath();
        if ( workspacePath == null ) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( workspacePath );
        stringBuffer.append( File.separator );
        stringBuffer.append( "contents.xcworkspacedata" );
        return stringBuffer.toString();
    }

    public static final String getBundleVersion() {

        String versionString = ProjectService.getProject().getVersion();
        if ( !versionString.contains( "SNAPSHOT" ) ) {
            return versionString;
        }
        return ProjectService.generateSnapshotVersion();
    }

    public static final Plist readInfoPlist( File file ) {

        if ( file.exists() ) {
            return PlistService.readPlist( file );
        }
        return null;
    }

    public static final String getConvertedInfoPlistPath( String target ) {

        StringBuffer plistPath = new StringBuffer();
        plistPath.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
        plistPath.append( File.separator );
        plistPath.append( InfoPlistService.INFO_PLIST );
        return plistPath.toString();
    }

    public static final File getEmbeddedInfoPlist( String target ) {

        return new File( getEmbeddedInfoPlistPath( target ) );
    }

    public static final String getArchivePath( String target ) {

        if ( !isArchiveAction( target ) ) {
            return null;
        }

        target = getBuildTargetName( target );
        Properties environmentProperties = PropertiesService.getBuildEnvironmentProperties( target );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( environmentProperties.get( BUILD_ARCHIVE_PATH ) );
        return stringBuffer.toString();
    }

    public static final boolean isArchiveAction( String target ) {

        Properties buildSettings = PropertiesService.getBuildSettings( target );
        String action = (String) buildSettings.get( BUILD_ACTION );
        return ACTION_ARCHIVE.equals( action );
    }

    public static final String getProductDirPath( String target ) {

        if ( isArchiveAction( target ) ) {

            target = getBuildTargetName( target );
            Properties environmentProperties = PropertiesService.getBuildEnvironmentProperties( target );

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( environmentProperties.get( BUILD_ARCHIVE_PRODUCTS_PATH ) );
            stringBuffer.append( File.separator );
            stringBuffer.append( "Applications" );
            return stringBuffer.toString();
        }

        Properties properties;

        // environment properties report configuration specified in scheme and not what we ran
//        if ( scheme != null ) {
//            properties = PropertiesService.getBuildEnvironmentProperties( target );
//        }
//        else {
        properties = PropertiesService.getBuildSettings( target );
//        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( properties.get( BUILD_BUILT_PRODUCTS_DIR ) );
        return stringBuffer.toString();
    }

    public static final String getProductFilePath( String target ) {

        Properties properties;

        if ( scheme != null ) {
            properties = PropertiesService.getBuildEnvironmentProperties( target );
        }
        else {
            properties = PropertiesService.getBuildSettings( target );
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getProductDirPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( properties.get( BUILD_FULL_PRODUCT_NAME ) );
        return stringBuffer.toString();
    }

    public static final String getEmbeddedInfoPlistPath( String target ) {

        Properties properties;

        if ( scheme != null ) {
            properties = PropertiesService.getBuildEnvironmentProperties( target );
        }
        else {
            properties = PropertiesService.getBuildSettings( target );
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getProductDirPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( properties.get( BUILD_INFOPLIST_PATH ) );
        return stringBuffer.toString();
    }

    public static final String getEmbeddedProvisoningProfilePath( String target ) {

        Properties properties;

        if ( scheme != null ) {
            properties = PropertiesService.getBuildEnvironmentProperties( target );
        }
        else {
            properties = PropertiesService.getBuildSettings( target );
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getProductFilePath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( properties.get( BUILD_EMBEDDED_PROFILE_NAME ) );
        return stringBuffer.toString();
    }

    public static final String getBuildTargetName( String target ) {

        Properties properties;

        if ( scheme != null && !isArchiveAction( target ) ) {
            properties = PropertiesService.getBuildEnvironmentProperties( target );
        }
        else {
            properties = PropertiesService.getBuildSettings( target );
        }

        return (String) properties.get( BUILD_TARGET_NAME );
    }

    public static final String getConfiguration( String target ) {

        if ( configuration == null ) {
            configuration = PropertiesService.getXCodeProperty( target, XCodeService.PROP_DEFAULT_CONFIG_NAME );
        }
        if ( configuration == null ) {
            configuration = PropertiesService.getXCodeProperty( XCodeService.PROP_DEFAULT_CONFIG_NAME );
        }
        return configuration;
    }

    public static final void setConfiguration( String configuration ) {

        XCodeService.configuration = configuration;
    }

    public static final String getCanonicalProjectFilePath( String referenceName ) {

        return fileIndex.get( referenceName );
    }

    public static final String getProjectFilePath( String referenceName ) {

        String canonicalPath = getCanonicalProjectFilePath( referenceName );
        if ( canonicalPath == null ) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( canonicalPath );
        return stringBuffer.toString();
    }

    public static final File getProjectFile( String referenceName ) {

        String projectPath = getProjectFilePath( referenceName );
        if ( projectPath == null ) {
            return null;
        }

        return new File( projectPath );
    }

    public static final void setFileIndex( Hashtable<String, String> fileIndex ) {

        XCodeService.fileIndex = fileIndex;
    }

    public static String[] getTargets() {

        return targets;
    }

    public static final void setTargets( String[] targets ) {

        XCodeService.targets = targets;
    }

    public static final String[] getSchemes() {

        return schemes;
    }

    public static String getScheme() {

        return scheme;
    }

    public static void setScheme( String scheme ) {

        XCodeService.scheme = scheme;
    }

    public static final String getProject() {

        return project;
    }

    public static void setProject( String project ) {

        XCodeService.project = project;
    }

    public static String getXcprojPath() {

        return projectPath;
    }

    public static void setXcprojPath( String projectPath ) {

        XCodeService.projectPath = projectPath;
    }

    public static String getWorkspace() {

        return workspace;
    }

    public static void setWorkspace( String workspace ) {

        XCodeService.workspace = workspace;
    }

    public static String getXcworkspacePath() {

        return workspacePath;
    }

    public static void setXcworkspacePath( String workspacePath ) {

        XCodeService.workspacePath = workspacePath;
    }

    public static String getKeychainPath() {

        return keychainPath;
    }

    public static void setKeychainPath( String keychainPath ) {

        XCodeService.keychainPath = keychainPath;
    }
}
