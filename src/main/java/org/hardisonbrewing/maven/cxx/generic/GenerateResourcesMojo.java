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

package org.hardisonbrewing.maven.cxx.generic;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Resource;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal generate-resources
 * @phase generate-resources
 */
public class GenerateResourcesMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        String generatedResourcesDirectory = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
        for (Resource resource : (List<Resource>) getProject().getResources()) {
            File resourceDirectory = new File( resource.getDirectory() );
            String[] filePaths = FileUtils.listFilePathsRecursive( resourceDirectory );
            for (String filePath : filePaths) {
                GenerateSourcesMojo.copyFile( filePath, resource.getDirectory(), generatedResourcesDirectory );
            }
        }
    }
}
