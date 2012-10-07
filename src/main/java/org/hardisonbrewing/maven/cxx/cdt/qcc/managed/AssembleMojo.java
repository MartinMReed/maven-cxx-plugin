/**
 * Copyright (c) 2011-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.cdt.qcc.managed;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;
import org.hardisonbrewing.maven.cxx.qnx.CommandLineService;

/**
 * @goal cdt-qcc-managed-assemble
 * @phase compile
 */
public class AssembleMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Configuration configuration = CProjectService.getBuildConfiguration( target );

        if ( !QccToolChain.matches( configuration ) ) {
            getLog().info( "Not a QCC project... skipping" );
            return;
        }

        QccToolChain toolChain = (QccToolChain) CdtService.getToolChain( configuration );
        Builder builder = toolChain.getBuilder();

        if ( builder.isMakefile() ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        String[] sources = ProjectService.getSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping assembler" );
            return;
        }

        QccToolChain.Options options = toolChain.getOptions();
        QccToolChain.Assembler assembler = toolChain.getAssembler();

        String compilerPlatform = options.getCompilerPlatform();
        boolean useDebug = assembler.isDebug();
        boolean useSecurity = assembler.useSecurity();
        boolean usePie = assembler.usePie();
        int optLevel = assembler.getOptLevel();
        boolean useProfile = assembler.useProfile();
        boolean useCodeCoverage = assembler.useCodeCoverage();

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
