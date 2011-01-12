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

package org.hardisonbrewing.maven.cxx.bar;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal bar-swf-compile
 * @phase compile
 */
public class SwfCompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        String artifactId = getProject().getArtifactId();

        if ( !shouldExecute() ) {
            getLog().info( artifactId + ".swf is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Building " + artifactId + ".swf..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "amxmlc" );

        cmd.add( "-output" );
        cmd.add( artifactId + ".swf" );

        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-compiler.debug" );
        }

        StringBuffer actionScriptPath = new StringBuffer();
        actionScriptPath.append( TargetDirectoryService.getGeneratedSourcesDirectoryPath() );
        actionScriptPath.append( File.separator );
        actionScriptPath.append( artifactId );
        actionScriptPath.append( ".as" );

        cmd.add( actionScriptPath.toString() );

        Commandline commandLine = buildCommandline( cmd );

        File sdkHome = PropertiesService.getPropertyAsFile( PropertiesService.BLACKBERRY_TABLET_HOME );
        if ( sdkHome != null ) {
            CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        }

        execute( commandLine );
    }

    private final boolean shouldExecute() {

        String swfFileName = getProject().getArtifactId() + ".swf";

        if ( PropertiesService.propertiesHaveChanged() ) {
            getLog().info( "Properties have changed, rebuilding " + swfFileName + "..." );
            return true;
        }

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( swfFileName );

        File outputFile = new File( outputPath.toString() );
        if ( outputFile.exists() ) {
            if ( outputFile.lastModified() >= getLatestFileDate() ) {
                return false;
            }
        }

        return true;
    }

    private final long getLatestFileDate() {

        long lastModified = 0;
        for (String filePath : TargetDirectoryService.getSourceFilePaths()) {
            lastModified = Math.max( lastModified, getLatestFileDate( filePath ) );
        }
        for (String filePath : TargetDirectoryService.getResourceFilePaths()) {
            lastModified = Math.max( lastModified, getLatestFileDate( filePath ) );
        }
        return lastModified;
    }

    private final long getLatestFileDate( String filePath ) {

        File sourceFile = new File( filePath );
        return FileUtils.lastModified( sourceFile, false );
    }
}
