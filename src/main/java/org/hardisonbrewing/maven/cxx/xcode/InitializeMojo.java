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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    /**
     * @parameter
     */
    public String configuration;

    /**
     * @parameter
     */
    public String codesignCertificate;

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        XCodeService.setConfiguration( configuration );

        if ( scheme != null ) {

            initWorkspace();
            XCodeService.setScheme( scheme );
            initWorkspaceProject( scheme );

            Properties properties = PropertiesService.getXCodeProperties();
            properties.put( XCodeService.PROP_SCHEME, scheme );
            PropertiesService.storeXCodeProperties( properties );
        }
        else {

            File project = XCodeService.loadProject();
            XCodeService.setXcprojPath( project.getPath() );

            String filename = project.getName();
            filename.substring( 0, filename.lastIndexOf( XCodeService.XCODEPROJ_EXTENSION ) - 1 );
            XCodeService.setProject( filename );
        }

        if ( provisioningProfile != null ) {
            ProvisioningProfileService.assertProvisioningProfile( provisioningProfile );
        }

        if ( codesignCertificate != null ) {
            CodesignCertificateService.assertCodesignCertificate( codesignCertificate );
        }
    }

    private void initWorkspace() {

        File workspace = XCodeService.loadWorkspace();
        XCodeService.setXcworkspacePath( workspace.getPath() );

        String workspaceName = workspace.getName();
        workspaceName.substring( 0, workspaceName.lastIndexOf( XCodeService.XCWORKSPACE_EXTENSION ) - 1 );
        XCodeService.setWorkspace( workspaceName );
    }

    private void initWorkspaceProject( String scheme ) {

        String projectPath = XCodeService.getSchemeXcprojPath( scheme );
        XCodeService.setXcprojPath( projectPath );

        int startIndex = projectPath.lastIndexOf( File.separatorChar );
        int endIndex = projectPath.lastIndexOf( XCodeService.XCODEPROJ_EXTENSION );
        String projectName = projectPath.substring( startIndex + 1, endIndex - 1 );
        XCodeService.setProject( projectName );
    }
}
