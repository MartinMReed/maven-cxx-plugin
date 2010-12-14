/**
 * Copyright (c) 2010 Martin M Reed
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

package org.hardisonbrewing.maven.cxx;

import java.io.File;
import java.io.IOException;

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

    /**
     * @parameter
     */
    public String[] sources;

    /**
     * @parameter
     */
    public String[] includes;

    @Override
    public final void execute() {

        copyFiles( null, ProjectService.getBaseDir().list() );
    }

    private final void copyFiles( String parentFileName, String[] fileNames ) {

        for (String fileName : fileNames) {
            StringBuffer srcChilePath = new StringBuffer();
            if ( parentFileName != null ) {
                srcChilePath.append( parentFileName );
                srcChilePath.append( File.separator );
            }
            srcChilePath.append( fileName );
            copyFile( srcChilePath.toString() );
        }
    }

    private final void copyFile( String fileName ) {

        if ( ".svn".equalsIgnoreCase( fileName ) ) {
            return;
        }
        if ( "target".equalsIgnoreCase( fileName ) ) {
            return;
        }

        StringBuffer srcPath = new StringBuffer();
        srcPath.append( ProjectService.getBaseDirPath() );
        srcPath.append( File.separator );
        srcPath.append( fileName );
        File src = new File( srcPath.toString() );

        StringBuffer destPath = new StringBuffer();
        destPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        destPath.append( File.separator );
        destPath.append( FileUtils.getProjectCanonicalPath( srcPath.toString() ) );
        File dest = new File( destPath.toString() );

        getLog().info( "Copying " + src + " to " + dest );

        if ( src.isDirectory() ) {
            dest.mkdir();
            copyFiles( fileName, src.list() );
        }
        else {
            try {
                FileUtils.copyFileToDirectory( src, dest.getParentFile() );
            }
            catch (IOException e) {
                throw new IllegalStateException( e.getMessage(), e );
            }
        }
    }
}
