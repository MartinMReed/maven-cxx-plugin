/**
 * Copyright (c) 2010-2011 Martin M Reed
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

import generated.Plist;

import java.io.File;
import java.util.Hashtable;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public final class XCodeService {

    public static final String XCODEPROJ_EXTENSION = "xcodeproj";

    private static String project;
    private static String configuration;

    private static Hashtable<String, String> fileIndex;

    private XCodeService() {

        // do nothing
    }

    public static boolean isApplicationType( String target ) {

        String productType = PropertiesService.getXCodeProperty( target, "productType" );
        return "com.apple.product-type.application".equals( productType );
    }

    public static String[] getTargets() {

        String targets = PropertiesService.getXCodeProperty( "targets" );
        return targets.split( "," );
    }

    public static final String getProject() {

        if ( project == null ) {
            File file = ProjectService.getBaseDir();
            for (String filePath : file.list()) {
                if ( filePath.endsWith( XCODEPROJ_EXTENSION ) ) {
                    project = filePath.substring( 0, filePath.lastIndexOf( XCODEPROJ_EXTENSION ) - 1 );
                    break;
                }
            }
        }
        return project;
    }

    public static final String getXcodeprojFilename() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getProject() );
        stringBuffer.append( "." );
        stringBuffer.append( XCODEPROJ_EXTENSION );
        return stringBuffer.toString();
    }

    public static final String getXcodeprojPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( getXcodeprojFilename() );
        return stringBuffer.toString();
    }

    public static final String getPbxprojPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( getXcodeprojFilename() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "project.pbxproj" );
        return stringBuffer.toString();
    }

    public static final String getBundleVersion() {

        String versionString = ProjectService.getProject().getVersion();
        if ( !versionString.contains( "SNAPSHOT" ) ) {
            return versionString;
        }
        return ProjectService.generateSnapshotVersion();
    }

    public static final String getBundleIdentifier() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getProject().getGroupId() );
        stringBuffer.append( "." );
        stringBuffer.append( ProjectService.getProject().getArtifactId() );
        return stringBuffer.toString();
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
        plistPath.append( "Info.plist" );
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
        plistPath.append( "Info.plist" );
        return plistPath.toString();
    }

    public static final String getConfiguration( String target ) {

        if ( configuration == null ) {
            configuration = PropertiesService.getXCodeProperty( target, "defaultConfigurationName" );
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
        JoJoMojo.getMojo().getLog().error( referenceName + " canonicalPath: " + canonicalPath );
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
}
