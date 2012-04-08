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
package org.hardisonbrewing.maven.cxx.qnx;

import generated.net.rim.bar.BarDescriptor;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal qnx-prepare-package
 * @phase prepare-package
 */
public final class PreparePackageMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();
        if ( barDescriptor != null ) {
            String barFilePath = TargetDirectoryService.getBarPath( barDescriptor );
            org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( barFilePath );
            return;
        }

        for (File file : TargetDirectoryService.getMakefileDirectories()) {

            String variant = file.getName();
            boolean staticLib = variant.matches( ".*[\\.]?a[\\.]?.*" );

            if ( !staticLib ) {
                continue;
            }

            String cpu = file.getParentFile().getName();

            File[] staticLibFiles = file.listFiles( (FileFilter) new WildcardFileFilter( "lib*.a" ) );
            for (File staticLibFile : staticLibFiles) {
                String filename = cpu + File.separator + staticLibFile.getName();
                org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( staticLibFile, filename );
            }
        }
    }
}
