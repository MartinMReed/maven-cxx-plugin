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

import javax.xml.bind.JAXBException;

import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class XCodeService {

    public static final String DEFAULT_CONFIGURATION = "default-configuration";
    public static final String XCODEPROJ_EXTENSION = ".xcodeproj";

    private static String project;

    public static final String getProject() {

        if ( project == null ) {
            File file = ProjectService.getBaseDir();
            for (String filePath : file.list()) {
                if ( filePath.endsWith( XCODEPROJ_EXTENSION ) ) {
                    project = filePath.substring( 0, filePath.lastIndexOf( XCODEPROJ_EXTENSION ) );
                    break;
                }
            }
        }
        return project;
    }

    public static final String getBundleVersion() {

        String versionString = ProjectService.getProject().getVersion();
        if ( !versionString.contains( "SNAPSHOT" ) ) {
            return versionString;
        }
        return ProjectService.generateSnapshotVersion();
    }

    public static final String getBundleIdentifier() {

        return ProjectService.getProject().getGroupId();
    }

    public static final Plist readInfoPlist() {

        File plistFile = getInfoPlist();

        if ( !plistFile.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate project PLIST file: " + plistFile );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( plistFile, Plist.class );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal PLIST file: " + plistFile );
            throw new IllegalStateException( e );
        }
    }

    public static final void writeInfoPlist( Plist plist ) {

        File plistFile = getInfoPlist();

        if ( !plistFile.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate project PLIST file: " + plistFile );
            throw new IllegalStateException();
        }

        try {
            JAXB.marshal( plistFile, plist );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to marshal PLIST file: " + plistFile );
            throw new IllegalStateException( e );
        }
    }

    private static final File getInfoPlist() {

        StringBuffer plistPath = new StringBuffer();
        plistPath.append( ProjectService.getBaseDirPath() );
        plistPath.append( File.separator );
        plistPath.append( getProject() );
        plistPath.append( "-Info.plist" );
        return new File( plistPath.toString() );
    }
}
