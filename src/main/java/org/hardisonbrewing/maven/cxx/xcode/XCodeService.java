/**
 * Copyright (c) 2010-2012 Martin M Reed
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
import java.util.List;

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
    public static final String PROP_SCHEME = "targets";

    private static String project;
    private static String projectPath;

    private static String workspace;
    private static String workspacePath;

    private static String[] schemes;
    private static String scheme;

    private static String configuration;

    private static Hashtable<String, String> fileIndex;

    private static List<String> targets;

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

        String schemePath = getSchemePath( scheme );
        if ( schemePath == null ) {
            return null;
        }

        int lastIndexOf = schemePath.lastIndexOf( XCODEPROJ_EXTENSION );
        schemePath = schemePath.substring( 0, lastIndexOf + XCODEPROJ_EXTENSION.length() );
        return schemePath;
    }

    public static final String getSchemePath( String scheme ) {

        File[] files = listSchemes();
        if ( files == null ) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( scheme );
        stringBuffer.append( "." );
        stringBuffer.append( XCSCHEME_EXTENSION );
        String filename = stringBuffer.toString();

        for (File file : files) {
            if ( filename.equals( file.getName() ) ) {
                return file.getPath();
            }
        }

        return null;
    }

    public static final void loadSchemes() {

        File[] files = listSchemes();
        String[] schemes = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            filename = filename.substring( 0, filename.lastIndexOf( XCSCHEME_EXTENSION ) - 1 );
            schemes[i] = filename;
        }

        XCodeService.schemes = schemes;
    }

    private static final File[] listSchemes() {

        StringBuffer extensionInclude = new StringBuffer();
        extensionInclude.append( "**/*.xcodeproj" );
        extensionInclude.append( File.separator );
        extensionInclude.append( "xcshareddata" );
        extensionInclude.append( File.separator );
        extensionInclude.append( "xcschemes" );
        extensionInclude.append( File.separator );
        extensionInclude.append( "*." );
        extensionInclude.append( XCSCHEME_EXTENSION );

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { extensionInclude.toString() };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
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

    public static final File getConvertedInfoPlist( String target ) {

        return new File( getConvertedInfoPlistPath( target ) );
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

    public static final String getEmbeddedInfoPlistPath( String target ) {

        StringBuffer plistPath = new StringBuffer();
        plistPath.append( TargetDirectoryService.getConfigBuildDirPath( target ) );
        plistPath.append( File.separator );
        plistPath.append( PropertiesService.getTargetProductName( target ) );
        plistPath.append( File.separator );
        plistPath.append( InfoPlistService.INFO_PLIST );
        return plistPath.toString();
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

    public static List<String> getTargets() {

        return targets;
    }

    public static final void setTargets( List<String> targets ) {

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
}
