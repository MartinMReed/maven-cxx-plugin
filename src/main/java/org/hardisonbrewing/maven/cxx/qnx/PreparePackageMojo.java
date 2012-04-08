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

        for (File file : TargetDirectoryService.getMakefileDirectories()) {

            String cpu = file.getParentFile().getName();
            String variant = file.getName();

            boolean staticLib = variant.matches( ".*[\\.]?a[\\.]?.*" );
            boolean application = variant.matches( ".*[\\.]?o[\\.]?.*" );

            if ( staticLib ) {
                File[] staticLibFiles = file.listFiles( (FileFilter) new WildcardFileFilter( "lib*.a" ) );
                for (File staticLibFile : staticLibFiles) {
                    String filename = cpu + File.separator + staticLibFile.getName();
                    org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( staticLibFile, filename );
                }
            }
            else if ( application ) {
                throw new UnsupportedOperationException( "Application currently not supported" );
            }
        }
    }
}
