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
package org.hardisonbrewing.maven.cxx.swf;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal air-swf-compile
 * @phase compile
 */
public class AirCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private KeyStore keystore;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();
        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        StringBuffer airFilePath = new StringBuffer();
        airFilePath.append( targetDirectoryPath );
        airFilePath.append( File.separator );
        airFilePath.append( artifactId );
        airFilePath.append( ".air" );

        getLog().info( "Building " + airFilePath.toString() + ".air..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "adt" );

        cmd.add( "-package" );

        cmd.add( "-storetype" );
        cmd.add( "pkcs12" );

        StringBuffer keystorePath = new StringBuffer();
        keystorePath.append( ProjectService.getBaseDirPath() );
        keystorePath.append( File.separator );
        keystorePath.append( keystore.keystore );
        cmd.add( "-keystore" );
        cmd.add( keystorePath.toString() );

        cmd.add( "-storepass" );
        cmd.add( keystore.storepass );

        if ( keystore.alias != null && keystore.alias.length() > 0 ) {
            cmd.add( "-alias" );
            cmd.add( keystore.alias );
        }

        if ( keystore.keypass != null && keystore.keypass.length() > 0 ) {
            cmd.add( "-keypass" );
            cmd.add( keystore.keypass );
        }

        cmd.add( "-target" );
        cmd.add( "air" );

        cmd.add( airFilePath.toString() );

        StringBuffer airiFilePath = new StringBuffer();
        airiFilePath.append( targetDirectoryPath );
        airiFilePath.append( File.separator );
        airiFilePath.append( artifactId );
        airiFilePath.append( ".airi" );
        cmd.add( airiFilePath.toString() );

        String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }
}
