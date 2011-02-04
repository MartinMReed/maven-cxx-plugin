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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;

/**
 * @goal xcode-generate-resources
 * @phase xcode-generate-resources
 */
public class GenerateResourcesMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        StringBuffer plistPath = new StringBuffer();
        plistPath.append( ProjectService.getBaseDirPath() );
        plistPath.append( File.separator );
        plistPath.append( XCodeService.getProject() );
        plistPath.append( "-Info.plist" );
        File plistFile = new File( plistPath.toString() );

        if ( !plistFile.exists() ) {
            getLog().error( "Unable to locate project PLIST file: " + plistPath );
            throw new IllegalStateException();
        }

        Plist plist;
        try {
            plist = JAXB.unmarshal( plistFile, Plist.class );
        }
        catch (JAXBException e) {
            getLog().error( "Unable to unmarshal PLIST file: " + plistPath );
            throw new IllegalStateException( e );
        }

        String versionString = getProject().getVersion();

        String version;
        if ( !versionString.contains( "SNAPSHOT" ) ) {
            version = versionString;
        }
        else {
            version = ProjectService.generateSnapshotVersion();
        }

        PlistService.setString( plist, "CFBundleVersion", version );
        PlistService.setString( plist, "CFBundleShortVersionString", versionString );

        try {
            JAXB.marshal( plistFile, plist );
        }
        catch (JAXBException e) {
            getLog().error( "Unable to marshal PLIST file: " + plistPath );
            throw new IllegalStateException( e );
        }
    }
}
