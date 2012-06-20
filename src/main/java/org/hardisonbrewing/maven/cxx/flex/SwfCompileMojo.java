/**
 * Copyright (c) 2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.flex;

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
 * @goal flex-compile
 * @phase compile
 */
public class SwfCompileMojo extends JoJoMojoImpl {

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
    private String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String swfFilename = getSourceName();
        getLog().info( "Building " + swfFilename + ".swf..." );

        // http://livedocs.adobe.com/flex/3/html/help.html?content=compilers_14.html
        List<String> cmd = new LinkedList<String>();
        cmd.add( "mxmlc" );

        cmd.add( "-output" );
        cmd.add( swfFilename + ".swf" );

        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-compiler.debug" );
        }

        StringBuffer actionScriptPath = new StringBuffer();
        actionScriptPath.append( TargetDirectoryService.getGeneratedSourcesDirectoryPath() );
        actionScriptPath.append( File.separator );
        actionScriptPath.append( sourceFile );
        cmd.add( actionScriptPath.toString() );

        String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

        StringBuffer configPath = new StringBuffer();
        configPath.append( sdkHome );
        configPath.append( File.separator );
        configPath.append( "frameworks" );
        configPath.append( File.separator );
        if ( FlexService.isIosTarget( target ) || FlexService.isAndroidTarget( target ) ) {
            configPath.append( "airmobile-config.xml" );
        }
        else {
            configPath.append( "air-config.xml" );
        }
        cmd.add( "-load-config" );
        cmd.add( configPath.toString() );

        String artifactId = getProject().getArtifactId();
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

    private String getSourceName() {

        int indexOf = sourceFile.indexOf( '.' );
        return indexOf == -1 ? sourceFile : sourceFile.substring( 0, indexOf );
    }
}
