/**
 * Copyright (c) 2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.flex;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal flex-airi-compile
 * @phase compile
 */
public class AiriCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String sourceFile;

    /**
     * @parameter
     */
    private String descriptorFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();
        getLog().info( "Building " + artifactId + ".airi..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "adt" );

        cmd.add( "-prepare" );

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        StringBuffer airiFilePath = new StringBuffer();
        airiFilePath.append( targetDirectoryPath );
        airiFilePath.append( File.separator );
        airiFilePath.append( artifactId );
        airiFilePath.append( ".airi" );
        cmd.add( airiFilePath.toString() );

        StringBuffer descriptorFilePath = new StringBuffer();
        descriptorFilePath.append( ProjectService.getBaseDirPath() );
        descriptorFilePath.append( File.separator );
        descriptorFilePath.append( descriptorFile );
        cmd.add( descriptorFilePath.toString() );

        cmd.add( "-C" );
        cmd.add( TargetDirectoryService.getGeneratedResourcesDirectoryPath() );
        cmd.add( "." );

        cmd.add( "-C" );
        cmd.add( targetDirectoryPath );
        cmd.add( getSourceName() + ".swf" );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addFlexEnvVars( commandLine );
        execute( commandLine );
    }

    private String getSourceName() {

        int indexOf = sourceFile.indexOf( '.' );
        return indexOf == -1 ? sourceFile : sourceFile.substring( 0, indexOf );
    }
}
