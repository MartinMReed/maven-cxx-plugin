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
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Resource;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.TargetDirectoryService;

/**
 * @goal generate-sources
 * @phase generate-sources
 * @requiresDependencyResolution generate-sources
 */
public final class GenerateSourcesMojo extends JoJoMojoImpl {

    @Override
    public final void execute() {

        copyFiles( null, ProjectService.getSourceFilePaths(), ProjectService.getSourceDirectoryPath() );
        copyResourceFilePaths();
    }

    public final void copyResourceFilePaths() {

        String filePathPrefix = ProjectService.getBaseDirPath();
        for (Resource resource : (List<Resource>) getProject().getResources()) {
            File resourceDirectory = new File( resource.getDirectory() );
            String[] filePaths = FileUtils.listFilePathsRecursive( resourceDirectory );
            for (String filePath : filePaths) {
                copyFile( filePath, resource.getDirectory() );
            }
        }
    }

    private final void copyFiles( String parentFileName, String[] fileNames, String filePathPrefix ) {

        for (String fileName : fileNames) {
            StringBuffer srcChildPath = new StringBuffer();
            if ( parentFileName != null ) {
                srcChildPath.append( parentFileName );
                srcChildPath.append( File.separator );
            }
            srcChildPath.append( fileName );
            copyFile( srcChildPath.toString(), filePathPrefix );
        }
    }

    private final void copyFile( String fileName, String filePathPrefix ) {

        if ( ".svn".equalsIgnoreCase( fileName ) ) {
            return;
        }

        File src = new File( fileName );

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();
        String destPath = fileName.replace( filePathPrefix, targetDirectoryPath );
        File dest = new File( destPath );

        getLog().info( "Copying " + src + " to " + dest );

        if ( src.isDirectory() ) {
            dest.mkdir();
            copyFiles( fileName, src.list(), filePathPrefix );
        }
        else {
            try {
                FileUtils.copyFileToDirectory( src, dest.getParentFile() );
            }
            catch (IOException e) {
                throw new IllegalStateException( e.getMessage(), e );
            }
        }

        dest.setLastModified( src.lastModified() );
    }
}
