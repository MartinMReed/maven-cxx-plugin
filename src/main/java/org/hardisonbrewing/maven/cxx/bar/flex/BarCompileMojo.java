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
package org.hardisonbrewing.maven.cxx.bar.flex;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.bar.LaunchMojo;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;
import org.hardisonbrewing.maven.cxx.bar.TargetDirectoryService;

/**
 * @goal flex-bar-compile
 * @phase compile
 */
public class BarCompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        String artifactId = getProject().getArtifactId();

        if ( !shouldExecute() ) {
            getLog().info( artifactId + ".bar is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Building " + artifactId + ".bar..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "blackberry-airpackager" );

        // this is only here to ensure the entry point in the Manifest shows as AIRDebug
        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-connect" );
            cmd.add( LaunchMojo.getIpAddress() );
        }

        cmd.add( "-package" );
        cmd.add( artifactId + ".bar" );

        cmd.add( artifactId + ".xml" );
        cmd.add( artifactId + ".swf" );

        File tabletXmlFile = TargetDirectoryService.getAppDescriptorFile();
        if ( tabletXmlFile.exists() ) {
            cmd.add( TargetDirectoryService.APP_DESCRIPTOR_FILENAME );
        }

        cmd.add( "-e" );
        cmd.add( TargetDirectoryService.RESOURCES_DIRECTORY );
        cmd.add( "." );

        Commandline commandLine = buildCommandline( cmd );

        String sdkHome = PropertiesService.getProperty( PropertiesService.BLACKBERRY_TABLET_HOME );
        if ( sdkHome != null ) {
            CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        }

        execute( commandLine );
    }

    private final boolean shouldExecute() {

        String barFileName = getProject().getArtifactId() + ".bar";

        if ( PropertiesService.propertiesHaveChanged() ) {
            getLog().info( "Properties have changed, rebuilding " + barFileName + "..." );
            return true;
        }

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( barFileName );

        File outputFile = new File( outputPath.toString() );
        if ( outputFile.exists() ) {
            if ( outputFile.lastModified() >= getLatestFileDate() ) {
                return false;
            }
        }

        return true;
    }

    private final long getLatestFileDate() {

        String artifactId = getProject().getArtifactId();
        long artifactXml = getLatestFileDate( artifactId + ".xml" );
        long artifactSwf = getLatestFileDate( artifactId + ".swf" );
        return Math.max( artifactXml, artifactSwf );
    }

    private final long getLatestFileDate( String filename ) {

        StringBuffer sourcePath = new StringBuffer();
        sourcePath.append( TargetDirectoryService.getTargetDirectoryPath() );
        sourcePath.append( File.separator );
        sourcePath.append( filename );

        File sourceFile = new File( sourcePath.toString() );
        return sourceFile.lastModified();
    }
}
