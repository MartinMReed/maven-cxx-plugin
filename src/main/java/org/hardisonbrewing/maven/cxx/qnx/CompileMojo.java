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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
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

        String[] sources = TargetDirectoryService.getProcessableSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping compiler" );
            return;
        }

        ToolChain toolChain = QnxService.getToolChain( target );

        String[] includes = QnxService.getCompilerIncludePaths( toolChain );
        String[] defines = QnxService.getCompilerDefines( toolChain );

        for (String source : sources) {

            List<String> cmd = new LinkedList<String>();
            cmd.add( "qcc" );

            cmd.add( "-o" );
            cmd.add( SourceFiles.replaceExtension( source, "s" ) );

            cmd.add( "-S" );
            cmd.add( SourceFiles.escapeFileName( source ) );

            if ( includes != null ) {
                for (String include : includes) {
                    include = PropertiesService.populateTemplateVariables( include, "${", "}" );
                    cmd.add( "-I" + include );
                }
            }

            //FIXME: grrrr
            cmd.add( "-V4.4.2,gcc_ntoarmv7le" );
            cmd.add( "-w1" );
            cmd.add( "-O2" );
            cmd.add( "-fstack-protector-all" );
            cmd.add( "-fPIE" );

            if ( defines != null ) {
                for (String define : defines) {
                    cmd.add( "-D" + define );
                }
            }

            Commandline commandLine = buildCommandline( cmd );
            CommandLineService.appendEnvVar( commandLine, "PATH", QnxService.getQnxHostBinPath() );
            commandLine.addEnvironment( "QNX_HOST", QnxService.getQnxHostDirPath() );
            commandLine.addEnvironment( "QNX_TARGET", QnxService.getQnxTargetDirPath() );
            execute( commandLine );
        }
    }
}
