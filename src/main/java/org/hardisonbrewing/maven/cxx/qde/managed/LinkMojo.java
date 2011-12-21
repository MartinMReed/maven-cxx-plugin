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

import generated.net.rim.bar.Asset;
import generated.net.rim.bar.BarDescriptor;
import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.io.File;
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
import org.hardisonbrewing.maven.cxx.qde.BarDescriptorService;
import org.hardisonbrewing.maven.cxx.qde.CProjectService;
import org.hardisonbrewing.maven.cxx.qde.CommandLineService;
import org.hardisonbrewing.maven.cxx.qde.PropertiesService;

/**
 * @goal qde-managed-link
 * @phase compile
 */
public final class LinkMojo extends JoJoMojoImpl {

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
            getLog().info( "No sources found... skipping linker" );
            return;
        }

        ToolChain toolChain = CProjectService.getToolChain( target );
        Tool tool = CProjectService.getTool( toolChain, CProjectService.QCC_TOOL_LINKER );

        String buildFilePath = getBuildFilePath();
        FileUtils.ensureParentExists( buildFilePath );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "qcc" );

        cmd.add( "-o" );
        cmd.add( buildFilePath );

        for (String source : sources) {
            source = TargetDirectoryService.resolveProcessedFilePath( source );
            cmd.add( SourceFiles.replaceExtension( source, "o" ) );
        }

        String[] libIncludes = CProjectService.getLinkerLibraryPaths( toolChain );
        if ( libIncludes != null ) {
            for (String include : libIncludes) {
                include = PropertiesService.populateTemplateVariables( include, "${", "}" );
                cmd.add( "-L" + include );
            }
        }

        String[] libs = CProjectService.getLinkerLibraries( toolChain );
        if ( libs != null ) {
            for (String lib : libs) {
                cmd.add( "-l" + lib );
            }
        }

        cmd.add( "-V" + CProjectService.getCompilerPlatform( toolChain ) );

        if ( CProjectService.isDebug( tool ) ) {
            cmd.add( "-g" );
        }

        cmd.add( "-Wl,-z,relro" );
        cmd.add( "-Wl,-z,now" );

        if ( CProjectService.useCodeCoverage( tool ) ) {
            cmd.add( "-ftest-coverage" );
            cmd.add( "-fprofile-arcs" );
            cmd.add( "-p" );
        }

        if ( CProjectService.useProfile( tool ) ) {
            cmd.add( "-lprofilingS" );
        }

        if ( CProjectService.usePie( tool ) ) {
            cmd.add( "-pie" );
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addQnxEnvVars( commandLine );
        execute( commandLine );
    }

    private final String getBuildFilePath() {

        BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();
        Asset entryPoint = BarDescriptorService.getEntryPoint( barDescriptor, target );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( CProjectService.getBuildPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( entryPoint.getValue() );
        return stringBuffer.toString();
    }
}
