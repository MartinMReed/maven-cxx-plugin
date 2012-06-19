/**
 * Copyright (c) 2010-2011 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.swf.mxml;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal mxml-swf-compile
 * @phase compile
 */
public class Mxml2SwfCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String libDirectory;

    /**
     * @parameter
     */
    private String sourceFile;

    /**
     * @parameter
     */
    private String configFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();
        getLog().info( "Building " + artifactId + ".swf..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "mxmlc" );

        cmd.add( "-output" );
        cmd.add( artifactId + ".swf" );

        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-compiler.debug" );
        }

        StringBuffer actionScriptPath = new StringBuffer();
        actionScriptPath.append( TargetDirectoryService.getGeneratedSourcesDirectoryPath() );
        actionScriptPath.append( File.separator );
        if ( sourceFile != null && sourceFile.length() > 0 ) {
            actionScriptPath.append( sourceFile );
        }
        else {
            actionScriptPath.append( artifactId );
            actionScriptPath.append( ".mxml" );
        }
        cmd.add( actionScriptPath.toString() );

        String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

        StringBuffer configPath = new StringBuffer();
        configPath.append( sdkHome );
        configPath.append( File.separator );
        configPath.append( "frameworks" );
        configPath.append( File.separator );
        if ( configFile != null && configFile.length() > 0 ) {
            configPath.append( configFile );
        }
        else {
            configPath.append( "air-config.xml" );
        }
        cmd.add( "-load-config" );
        cmd.add( configPath.toString() );

        cmd.add( "-include-libraries+=" + artifactId + ".swc" );

        if ( libDirectory != null && libDirectory.length() > 0 ) {
            String[] includes = new String[] { "*.swc" };
            File[] files = FileUtils.listFilesRecursive( new File( libDirectory ), includes, null );
            for (File file : files) {
                cmd.add( "-include-libraries+=" + file.getAbsolutePath() );
            }
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }
}
