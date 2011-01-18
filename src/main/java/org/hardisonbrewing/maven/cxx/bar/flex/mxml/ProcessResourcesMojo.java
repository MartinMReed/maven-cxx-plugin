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

package org.hardisonbrewing.maven.cxx.bar.flex.mxml;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;
import org.hardisonbrewing.maven.cxx.bar.TargetDirectoryService;

/**
 * @goal mxml-process-resources
 * @phase process-resources
 */
public class ProcessResourcesMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        String artifactId = getProject().getArtifactId();

        if ( !shouldExecute() ) {
            getLog().info( artifactId + ".swc is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Building " + artifactId + ".swc..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "compc" );

        cmd.add( "-output" );
        cmd.add( artifactId + ".swc" );

        String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

        StringBuffer configPath = new StringBuffer();
        configPath.append( sdkHome );
        configPath.append( File.separator );
        configPath.append( "frameworks" );
        configPath.append( File.separator );
        configPath.append( "airmobile-config.xml" );
        cmd.add( "-load-config" );
        cmd.add( configPath.toString() );

        String generatedResourcesDirectoryPath = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();
        for (File file : TargetDirectoryService.getResourceFiles()) {
            cmd.add( "-include-file" );
            cmd.add( FileUtils.getCanonicalPath( file.getPath(), generatedResourcesDirectoryPath ) );
            cmd.add( FileUtils.getCanonicalPath( file.getPath(), targetDirectoryPath ) );
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }

    protected final boolean shouldExecute() {

        String swcFileName = getProject().getArtifactId() + ".swc";

        if ( PropertiesService.propertiesHaveChanged() ) {
            getLog().info( "Properties have changed, rebuilding " + swcFileName + "..." );
            return true;
        }

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( swcFileName );

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
