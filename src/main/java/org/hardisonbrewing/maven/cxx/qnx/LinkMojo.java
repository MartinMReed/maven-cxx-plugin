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

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;

/**
 * @goal o-qnx-link
 * @phase compile
 */
public final class LinkMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() {

        String[] sources = TargetDirectoryService.getProcessableSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping linker" );
            return;
        }

        ToolChain toolChain = QnxService.getToolChain( target );
        Tool tool = CProjectService.getTool( toolChain, QnxService.QCC_TOOL_LINKER );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "qcc" );

        cmd.add( "-o" );
        cmd.add( getProject().getArtifactId() + ".o" );

        for (String source : sources) {
            cmd.add( SourceFiles.replaceExtension( source, "o" ) );
        }

        String[] libIncludes = QnxService.getLinkerLibraryPaths( toolChain );
        if ( libIncludes != null ) {
            for (String include : libIncludes) {
                include = PropertiesService.populateTemplateVariables( include, "${", "}" );
                cmd.add( "-L" + include );
            }
        }

        String[] libs = QnxService.getLinkerLibraries( toolChain );
        if ( libs != null ) {
            for (String lib : libs) {
                cmd.add( "-l" + lib );
            }
        }

        cmd.add( "-V" + QnxService.getCompilerPlatform( toolChain ) );

        if ( QnxService.isDebug( tool ) ) {
            cmd.add( "-g" );
        }

        cmd.add( "-Wl,-z,relro" );
        cmd.add( "-Wl,-z,now" );

        if ( QnxService.useCodeCoverage( tool ) ) {
            cmd.add( "-ftest-coverage" );
            cmd.add( "-fprofile-arcs" );
            cmd.add( "-p" );
        }

        if ( QnxService.useProfile( tool ) ) {
            cmd.add( "-lprofilingS" );
        }

        if ( QnxService.usePie( tool ) ) {
            cmd.add( "-pie" );
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", QnxService.getQnxHostBinPath() );
        commandLine.addEnvironment( "QNX_HOST", QnxService.getQnxHostDirPath() );
        commandLine.addEnvironment( "QNX_TARGET", QnxService.getQnxTargetDirPath() );
        execute( commandLine );
    }
}
