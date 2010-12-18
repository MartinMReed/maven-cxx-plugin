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
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TargetDirectoryService;

public abstract class PreparePackageMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String[] includes;

    protected String classifier;

    protected PreparePackageMojo() {

        // do nothing
    }

    @Override
    public void execute() {

        prepareArtifact();

        for (int i = 0; includes != null && i < includes.length; i++) {
            prepareTargetFile( includes[i] );
        }
    }

    protected void prepareArtifact() {

        String artifactId = getProject().getArtifactId();
        prepareTargetFile( artifactId + "." + classifier );
    }

    public static final void prepareTargetFile( String fileName ) {

        StringBuffer srcPath = new StringBuffer();
        srcPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        srcPath.append( File.separator );
        srcPath.append( fileName );
        File src = new File( srcPath.toString() );
        if ( !src.exists() ) {
            throw new IllegalStateException( src.getAbsolutePath() + " does not exist." );
        }

        prepareTargetFile( src, FileUtils.getProjectCanonicalPath( src.getPath() ) );
    }

    public static final void prepareTargetFile( File src, String fileName ) {

        StringBuffer destPath = new StringBuffer();
        destPath.append( TargetDirectoryService.getTempPackagePath() );
        destPath.append( File.separator );
        destPath.append( fileName );
        File dest = new File( destPath.toString() );

        JoJoMojo.getMojo().getLog().info( "Copying " + src + " to " + dest );

        try {
            FileUtils.copyFileToDirectory( src, dest.getParentFile() );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
