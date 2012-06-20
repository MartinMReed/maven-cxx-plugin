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
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;
import org.hardisonbrewing.maven.cxx.bar.TargetDirectoryService;

/**
 * @goal flex-swc-compile
 * @phase compile
 */
public class SwcCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();
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
        if ( FlexService.isIosTarget( target ) || FlexService.isAndroidTarget( target ) ) {
            configPath.append( "airmobile-config.xml" );
        }
        else {
            configPath.append( "air-config.xml" );
        }
        cmd.add( "-load-config" );
        cmd.add( configPath.toString() );

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();
        String generatedResourcesDirectoryPath = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
        for (File file : TargetDirectoryService.getResourceFiles()) {
            cmd.add( "-include-file" );
            cmd.add( FileUtils.getCanonicalPath( file.getPath(), generatedResourcesDirectoryPath ) );
            cmd.add( FileUtils.getCanonicalPath( file.getPath(), targetDirectoryPath ) );
        }

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }
}
