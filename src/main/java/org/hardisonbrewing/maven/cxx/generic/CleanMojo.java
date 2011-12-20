/**
 * Copyright (c) 2011 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.generic;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal clean
 * @phase clean
 */
public final class CleanMojo extends JoJoMojoImpl {

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        String[] targetDirectories = TargetDirectoryService.getTargetCleaupDirectories();

        if ( targetDirectories != null ) {

            for (String targetDirectory : targetDirectories) {

                StringBuffer filePath = new StringBuffer();
                if ( FileUtils.isCanonical( targetDirectory ) ) {
                    filePath.append( ProjectService.getBaseDirPath() );
                    filePath.append( File.separator );
                }
                filePath.append( targetDirectory );

                File file = new File( filePath.toString() );

                if ( !file.exists() ) {
                    continue;
                }

                try {
                    FileUtils.deleteDirectory( file );
                }
                catch (IOException e) {
                    getLog().error( "Unable to delete directory: " + file );
                    throw new IllegalStateException();
                }
            }
        }
    }
}
