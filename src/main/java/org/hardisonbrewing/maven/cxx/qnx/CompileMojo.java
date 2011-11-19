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
package org.hardisonbrewing.maven.cxx.qnx;

import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal o-qnx-compile
 * @phase compile
 */
public class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String[] sources = ProjectService.getSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping compiler" );
            return;
        }

        ToolChain toolChain = CProjectService.getToolChain( target );
        Tool tool = CProjectService.getTool( toolChain, CProjectService.QCC_TOOL_COMPILER );

        String compilerPlatform = CProjectService.getCompilerPlatform( toolChain );
        boolean usePie = CProjectService.usePie( tool );
        boolean useSecurity = CProjectService.useSecurity( tool );
        boolean useDebug = CProjectService.isDebug( tool );
        int optLevel = CProjectService.getOptLevel( tool );
        boolean useProfile = CProjectService.useProfile( tool );
        boolean useCodeCoverage = CProjectService.useCodeCoverage( tool );
        String[] includes = CProjectService.getCompilerIncludePaths( toolChain );
        String[] defines = CProjectService.getCompilerDefines( toolChain );

        for (String source : sources) {

            String processedSource = TargetDirectoryService.resolveProcessedFilePath( source );
            FileUtils.ensureParentExists( processedSource );

            List<String> cmd = new LinkedList<String>();
            cmd.add( "qcc" );

            cmd.add( "-o" );
            cmd.add( SourceFiles.replaceExtension( processedSource, "s" ) );

            cmd.add( "-S" );
            cmd.add( SourceFiles.escapeFileName( source ) );

            if ( includes != null ) {
                for (String include : includes) {
                    include = PropertiesService.populateTemplateVariables( include, "${", "}" );
                    cmd.add( "-I" + include );
                }
            }

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

            if ( defines != null ) {
                for (String define : defines) {
                    cmd.add( "-D" + define );
                }
            }

            Commandline commandLine = buildCommandline( cmd );
            CommandLineService.addQnxEnvVars( commandLine );
            execute( commandLine );
        }
    }
}
