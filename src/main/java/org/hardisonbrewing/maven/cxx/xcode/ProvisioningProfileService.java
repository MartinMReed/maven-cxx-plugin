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

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class ProvisioningProfileService {

    public static final String PROVISIONING_PROFILES = "Library/MobileDevice/Provisioning Profiles";

    public static File getProvisioningProfile( String name ) {

        if ( name == null ) {
            JoJoMojo.getMojo().getLog().info( "Provisioning profile not specified, skipping." );
        }

        StringBuffer filePath = new StringBuffer();
        filePath.append( ProjectService.getBaseDirPath() );
        filePath.append( File.separator );
        filePath.append( name );
        return new File( filePath.toString() );
    }

    public static void assertProvisioningProfile( String name ) {

        File file = getProvisioningProfile( name );

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate provisioning profile: " + file );
            throw new IllegalStateException();
        }
    }
}
