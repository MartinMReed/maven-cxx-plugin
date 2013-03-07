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
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal msbuild-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "msbuild" );
        cmd.add( MSBuildService.getProjectFilePath() );
        cmd.add( "/p:OutDir=" + TargetDirectoryService.getBinDirectoryPath() );

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
