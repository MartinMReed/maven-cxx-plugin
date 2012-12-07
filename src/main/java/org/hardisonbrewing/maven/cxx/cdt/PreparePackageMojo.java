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
package org.hardisonbrewing.maven.cxx.cdt;

import generated.net.rim.bar.BarDescriptor;
import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.qnx.BarDescriptorService;
import org.hardisonbrewing.maven.cxx.qnx.TargetDirectoryService;

/**
 * @goal cdt-prepare-package
 * @phase prepare-package
 */
public class PreparePackageMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Configuration configuration = CProjectService.getBuildConfiguration( target );

        if ( QccToolChain.matches( configuration ) ) {
            BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();
            String barFilePath = TargetDirectoryService.getBarPath( barDescriptor );
            org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( barFilePath );
        }
        else if ( CProjectService.isStaticLib( configuration ) ) {

            String buildFilePath = CProjectService.getBuildFilePath( configuration );
            File buildFile = new File( buildFilePath );
            org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( buildFile, buildFile.getName() );
        }
    }
}
