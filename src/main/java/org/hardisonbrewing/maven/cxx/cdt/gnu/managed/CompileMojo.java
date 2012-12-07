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
package org.hardisonbrewing.maven.cxx.cdt.gnu.managed;

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
import org.hardisonbrewing.maven.cxx.cdt.toolchain.GnuToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;

/**
 * @goal cdt-gnu-managed-compile
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

        if ( !GnuToolChain.matches( configuration ) ) {
            getLog().info( "Not a GNU project... skipping" );
            return;
        }

        GnuToolChain toolChain = CdtService.getToolChain( configuration );
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

        GnuToolChain.Options options = toolChain.getOptions();

        // TODO: read plugin.xml to get c vs c++ file extensions:
        //  <language
        //        class="org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage"
        //        id="gcc"
        //        name="%language.name.gcc">
        //     <contentType id="org.eclipse.cdt.core.cSource"/>
        //     <contentType id="org.eclipse.cdt.core.cHeader"/>
        //  </language>
        //  <language
        //        class="org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage"
        //        id="g++"
        //        name="%language.name.gpp">
        //     <contentType id="org.eclipse.cdt.core.cxxSource"/>
        //     <contentType id="org.eclipse.cdt.core.cxxHeader"/>
        //  </language>

        for (String source : sources) {

            GnuToolChain.CppCompiler compiler = toolChain.getCppCompiler();

            int optLevel = compiler.getOptLevel();
            int debugLevel = compiler.getDebugLevel();
            boolean verbose = compiler.getVerbose();
            String otherOptions = compiler.getOtherOptions();
            String[] includes = compiler.getIncludePaths();
            String[] defines = compiler.getDefines();

            String processedSource = TargetDirectoryService.resolveProcessedFilePath( source );
            FileUtils.ensureParentExists( processedSource );

            List<String> cmd = new LinkedList<String>();
            cmd.add( "g++" );

            cmd.add( "-o" );
            cmd.add( SourceFiles.replaceExtension( processedSource, "s" ) );

            cmd.add( "-S" );
            cmd.add( SourceFiles.escapeFileName( source ) );

            if ( includes != null ) {
                for (String include : includes) {
                    if ( PropertiesService.isWorkspaceValue( include ) ) {
                        include = PropertiesService.getWorkspacePath( configuration, include );
                    }
                    cmd.add( "-I" + include );
                }
            }

            if ( optLevel > 0 ) {
                cmd.add( "-O" + optLevel );
            }

            if ( debugLevel > 0 ) {
                cmd.add( "-g" + debugLevel );
            }

            if ( verbose ) {
                cmd.add( "-v" );
            }

            if ( otherOptions != null && otherOptions.length() > 0 ) {
                cmd.add( otherOptions );
            }

            if ( defines != null ) {
                for (String define : defines) {
                    cmd.add( "-D" + define );
                }
            }

            Commandline commandLine = buildCommandline( cmd );
            commandLine.getShell().setQuotedArgumentsEnabled( false );
            execute( commandLine );
        }
    }
}
