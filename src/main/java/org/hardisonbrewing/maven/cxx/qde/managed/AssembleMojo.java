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
package org.hardisonbrewing.maven.cxx.qde.managed;

import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.qde.CProjectService;
import org.hardisonbrewing.maven.cxx.qde.CommandLineService;

/**
 * @goal qde-managed-assemble
 * @phase compile
 */
public class AssembleMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( CProjectService.isMakefileBuilder( target ) ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        String[] sources = ProjectService.getSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping assembler" );
            return;
        }

        ToolChain toolChain = CProjectService.getToolChain( target );
        Tool tool = CProjectService.getTool( toolChain, CProjectService.QCC_TOOL_ASSEMBLER );

        String compilerPlatform = CProjectService.getCompilerPlatform( toolChain );
        boolean useDebug = CProjectService.isDebug( tool );
        boolean useSecurity = CProjectService.useSecurity( tool );
        boolean usePie = CProjectService.usePie( tool );
        int optLevel = CProjectService.getOptLevel( tool );
        boolean useProfile = CProjectService.useProfile( tool );
        boolean useCodeCoverage = CProjectService.useCodeCoverage( tool );

        for (String source : sources) {

            source = TargetDirectoryService.resolveProcessedFilePath( source );

            List<String> cmd = new LinkedList<String>();
            cmd.add( "qcc" );

            cmd.add( "-o" );
            cmd.add( SourceFiles.replaceExtension( source, "o" ) );

            cmd.add( "-c" );
            cmd.add( SourceFiles.replaceExtension( source, "s" ) );

            cmd.add( "-V" + compilerPlatform );

            if ( optLevel != -1 ) {
                cmd.add( "-O" + optLevel );
            }

            if ( useDebug ) {
                cmd.add( "-g" );
            }

            if ( useSecurity ) {
                cmd.add( "-fstack-protector-all" );
            }

            if ( useCodeCoverage ) {
                cmd.add( "-Wc,-ftest-coverage" );
                cmd.add( "-Wc,-fprofile-arcs" );
            }

            if ( useProfile ) {
                cmd.add( "-finstrument-functions" );
            }

            if ( usePie ) {
                cmd.add( "-fPIE" );
            }

            Commandline commandLine = buildCommandline( cmd );
            CommandLineService.addQnxEnvVars( commandLine );
            execute( commandLine );
        }
    }
}
