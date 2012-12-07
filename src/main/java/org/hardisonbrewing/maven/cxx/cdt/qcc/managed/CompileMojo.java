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
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.PropertiesService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;
import org.hardisonbrewing.maven.cxx.qnx.CommandLineService;

/**
 * @goal cdt-qcc-managed-compile
 * @phase compile
 */
public class CompileMojo extends JoJoMojoImpl {

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

        QccToolChain toolChain = CdtService.getToolChain( configuration );
        Builder builder = toolChain.getBuilder();

        if ( builder.isMakefile() ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        String[] sources = ProjectService.getSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping compiler" );
            return;
        }

        QccToolChain.Options options = toolChain.getOptions();
        QccToolChain.Compiler compiler = toolChain.getCompiler();

        String compilerPlatform = options.getCompilerPlatform();
        boolean usePie = compiler.usePie();
        boolean useSecurity = compiler.useSecurity();
        boolean useDebug = compiler.isDebug();
        int optLevel = compiler.getOptLevel();
        boolean useProfile = compiler.useProfile();
        boolean useCodeCoverage = compiler.useCodeCoverage();
        String[] includes = compiler.getIncludePaths();
        String[] defines = compiler.getDefines();

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

            if ( optLevel > -1 ) {
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
