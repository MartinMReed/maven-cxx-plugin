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
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.PropertiesService;

/**
 * @goal xcode-install-provisioning-profile
 * @phase xcode-install-provisioning-profile
 */
public class InstallProvisioningProfileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( provisioningProfile == null ) {
            getLog().info( "Provisioning profile not specified, skipping." );
            return;
        }

        File file = ProvisioningProfileService.getProvisioningProfile( provisioningProfile );

        StringBuffer directoryPath = new StringBuffer();
        directoryPath.append( PropertiesService.getProperty( "user.home" ) );
        directoryPath.append( File.separator );
        directoryPath.append( ProvisioningProfileService.PROVISIONING_PROFILES );

        File directory = new File( directoryPath.toString() );
        if ( !directory.exists() ) {
            directory.mkdirs();
        }

        try {
            FileUtils.copyFileToDirectory( file, directory );
        }
        catch (IOException e) {
            getLog().error( "Unable to copy provisioning profile: " + file );
            throw new IllegalStateException();
        }
    }
}
