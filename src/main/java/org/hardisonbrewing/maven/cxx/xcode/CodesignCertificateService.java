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

import java.io.File;
import java.util.Properties;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class CodesignCertificateService {

    public static File getCertificateFile( String certificateFile ) {

        if ( certificateFile == null ) {

            JoJoMojo.getMojo().getLog().info( "Codesign certificate not specified, skipping." );
            return null;
        }

        String filePath;
        if ( certificateFile.startsWith( File.separator ) ) {

            filePath = certificateFile;
        }
        else if ( certificateFile.startsWith( "~/" ) ) {

            Properties properties = PropertiesService.getProperties();
            String userHome = properties.getProperty( "user.home" );
            filePath = userHome + certificateFile.substring( 1 );
        }
        else {

            StringBuffer filePathBuffer = new StringBuffer();
            filePathBuffer.append( ProjectService.getBaseDirPath() );
            filePathBuffer.append( File.separator );
            filePathBuffer.append( certificateFile );
            filePath = filePathBuffer.toString();
        }
        return new File( filePath.toString() );
    }

    public static void assertCodesignCertificate( String codesignCertificate ) {

        File file = getCertificateFile( codesignCertificate );
        if ( !file.exists() ) {

            JoJoMojo.getMojo().getLog().error( "Unable to locate codesign certificate: " + file );
            throw new IllegalStateException();
        }
    }
}
