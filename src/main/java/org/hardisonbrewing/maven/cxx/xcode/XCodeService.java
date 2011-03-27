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

import org.hardisonbrewing.maven.core.ProjectService;

public class XCodeService {

    public static final String XCODEPROJ_EXTENSION = "xcodeproj";

    private static String project;
    public static String target;
    private static String configuration;

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

    public static final String getPbxprojPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( getProject() );
        stringBuffer.append( "." );
        stringBuffer.append( XCODEPROJ_EXTENSION );
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

    public static final Plist readInfoPlist() {

        return PlistService.readPlist( getInfoPlist() );
    }

    public static final void writeInfoPlist( Plist plist ) {

        PlistService.writePlist( plist, getInfoPlist() );
    }

    private static final File getInfoPlist() {

        String infoPlistFile = PropertiesService.getXCodeProperty( getConfiguration(), "infoPlistFile" );

        StringBuffer plistPath = new StringBuffer();
        plistPath.append( ProjectService.getBaseDirPath() );
        plistPath.append( File.separator );
        plistPath.append( infoPlistFile );
        return new File( plistPath.toString() );
    }

    public static final String getConfiguration() {

        if ( configuration == null ) {
            configuration = PropertiesService.getXCodeProperty( target, "defaultConfigurationName" );
        }
        return configuration;
    }

    public static final void setConfiguration( String configuration ) {

        XCodeService.configuration = configuration;
    }
}
