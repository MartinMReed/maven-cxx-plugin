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
package org.hardisonbrewing.maven.cxx.bar.js;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.hardisonbrewing.maven.core.ArchiveService;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal js-bar-generate-zip
 * @phase generate-sources
 */
public class GenerateZipMojo extends JoJoMojoImpl {

    public String classifier;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String generatedSourcesDirectoryPath = TargetDirectoryService.getGeneratedSourcesDirectoryPath();
        File generatedSourcesDirectory = new File( generatedSourcesDirectoryPath );
        for (String resourceFilePath : FileUtils.listFilePathsRecursive( generatedSourcesDirectory )) {
            File src = new File( resourceFilePath );
            prepareFile( src, FileUtils.getCanonicalPath( resourceFilePath, generatedSourcesDirectoryPath ) );
        }

        String generatedResourcesDirectoryPath = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
        File generatedResourcesDirectory = new File( generatedResourcesDirectoryPath );
        for (String resourceFilePath : FileUtils.listFilePathsRecursive( generatedResourcesDirectory )) {
            File src = new File( resourceFilePath );
            prepareFile( src, FileUtils.getCanonicalPath( resourceFilePath, generatedResourcesDirectoryPath ) );
        }

        File src = new File( getTempPackagePath() );
        File dest = new File( getTempPackagePath() + ".zip" );

        try {
            ArchiveService.archive( src, dest );
        }
        catch (ArchiverException e) {
            throw new IllegalStateException( e );
        }
    }

    private final void prepareFile( File src, String filename ) {

        if ( !src.exists() ) {
            getLog().error( src.getAbsolutePath() + " does not exist." );
            throw new IllegalStateException();
        }
        StringBuffer destPath = new StringBuffer();
        destPath.append( getTempPackagePath() );
        destPath.append( File.separator );
        destPath.append( filename );
        File dest = new File( destPath.toString() );

        JoJoMojo.getMojo().getLog().info( "Copying " + src + " to " + dest );

        try {
            FileUtils.copyFileToDirectory( src, dest.getParentFile() );
        }
        catch (IOException e) {
            throw new IllegalStateException( e );
        }
    }

    public static final String getTempPackagePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "bbwp" );
        return stringBuffer.toString();
    }
}
