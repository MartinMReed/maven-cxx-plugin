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

package org.hardisonbrewing.maven.cxx.bar;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Resource;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.generic.GenerateSourcesMojo;

/**
 * @goal bar-generate-resources
 * @phase bar-generate-resources
 */
public class GenerateResourcesMojo extends org.hardisonbrewing.maven.cxx.generic.GenerateResourcesMojo {

    /**
     * @parameter
     */
    public String icon;

    @Override
    public final void execute() {

        super.execute();

        if ( icon != null ) {
            resource_loop: for (Resource resource : (List<Resource>) getProject().getResources()) {
                String resourceDirectoryPath = resource.getDirectory();
                File resourceDirectory = new File( resourceDirectoryPath );
                String[] filePaths = FileUtils.listFilePathsRecursive( resourceDirectory );
                file_loop: for (String filePath : filePaths) {

                    // do a quick check to see if just the tail matches
                    if ( !filePath.endsWith( icon ) ) {
                        continue file_loop;
                    }

                    // do a full check to see if this is what we want
                    StringBuffer iconFilePath = new StringBuffer();
                    iconFilePath.append( resourceDirectory );
                    iconFilePath.append( File.separator );
                    iconFilePath.append( icon );
                    if ( !filePath.equals( iconFilePath.toString() ) ) {
                        continue file_loop;
                    }

                    String targetDirectory = TargetDirectoryService.getTargetDirectoryPath();
                    GenerateSourcesMojo.copyFile( filePath, resource.getDirectory(), targetDirectory );
                    break resource_loop;
                }
            }
        }
    }
}
