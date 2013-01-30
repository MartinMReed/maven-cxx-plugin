/**
 * Copyright (c) 2013 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.msbuild;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal msbuild-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "MSBuild" );

        cmd.add( project );

        cmd.add( "/p:OutDir=" + TargetDirectoryService.getBinDirectoryPath() );

        MavenProject project = getProject();

        StringBuffer xapFilename = new StringBuffer();
        xapFilename.append( project.getArtifactId() );
        xapFilename.append( "." );
        xapFilename.append( MSBuildService.XAP_EXTENSION );

        cmd.add( "/p:XapFilename=" + xapFilename.toString() );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );
        execute( commandLine );
    }

    @Override
    protected Commandline buildCommandline( List<String> cmd ) {

        try {
            return CommandLineService.build( cmd );
        }
        catch (CommandLineException e) {
            throw new IllegalStateException( e );
        }
    }
}
