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
 * @goal cdt-qcc-managed-link
 * @phase compile
 */
public final class LinkMojo extends JoJoMojoImpl {

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
            getLog().info( "No sources found... skipping linker" );
            return;
        }

        QccToolChain.Options options = toolChain.getOptions();
        QccToolChain.Linker linker = toolChain.getLinker();

        boolean staticLib = CProjectService.isStaticLib( configuration );
        boolean application = CProjectService.isApplication( configuration );

        String buildFilePath = CProjectService.getBuildFilePath( configuration );
        FileUtils.ensureParentExists( buildFilePath );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "qcc" );

        if ( staticLib ) {
            cmd.add( "-A" );
        }
        else {
            cmd.add( "-o" );
        }

        cmd.add( buildFilePath );

        for (String source : sources) {
            source = TargetDirectoryService.resolveProcessedFilePath( source );
            cmd.add( SourceFiles.replaceExtension( source, "o" ) );
        }

        if ( application ) {

            String[] libIncludes = linker.getLibraryPaths();
            if ( libIncludes != null ) {
                for (String include : libIncludes) {
                    include = PropertiesService.populateTemplateVariables( include, "${", "}" );
                    cmd.add( "-L" + include );
                }
            }

            String[] libs = linker.getLibraries();
            if ( libs != null ) {
                for (String lib : libs) {
                    cmd.add( "-l" + lib );
                }
            }
        }

        cmd.add( "-V" + options.getCompilerPlatform() );

        if ( linker.isDebug() ) {
            cmd.add( "-g" );
        }

        if ( application ) {

            cmd.add( "-Wl,-z,relro" );
            cmd.add( "-Wl,-z,now" );

            if ( linker.useCodeCoverage() ) {
                cmd.add( "-ftest-coverage" );
                cmd.add( "-fprofile-arcs" );
                cmd.add( "-p" );
            }

            if ( linker.useProfile() ) {
                cmd.add( "-lprofilingS" );
            }

            if ( linker.usePie() ) {
                cmd.add( "-pie" );
            }
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addQnxEnvVars( commandLine );
        execute( commandLine );
    }
}
