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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal flex-swc-compile
 * @phase compile
 */
public class SwcCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String target;

    /**
     * @parameter
     */
    private String config;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();

        String resourceConfigName = artifactId + "-swc-config.xml";

        StringBuffer resourceConfigPathBuffer = new StringBuffer();
        resourceConfigPathBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        resourceConfigPathBuffer.append( File.separator );
        resourceConfigPathBuffer.append( resourceConfigName );
        String resourceConfigPath = resourceConfigPathBuffer.toString();

        getLog().info( "Building " + resourceConfigName + "..." );

        try {
            writeResourceConfigFile( resourceConfigPath );
        }
        catch (IOException e) {
            getLog().error( "Unable to write SWC config file: " + resourceConfigPath );
            throw new IllegalStateException( e );
        }

        getLog().info( "Building " + artifactId + ".swc..." );

        // compc options: http://www.docsultant.com/site2/articles/flex_cmd.html
        List<String> cmd = new LinkedList<String>();
        cmd.add( "compc" );

        cmd.add( "-output" );
        cmd.add( artifactId + ".swc" );

        FlexService.addConfig( cmd, config, target );

        cmd.add( "-load-config" );
        cmd.add( resourceConfigPath );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addFlexEnvVars( commandLine );
        execute( commandLine );
    }

    private void writeResourceConfigFile( String configFilePath ) throws IOException {

        File configFile = new File( configFilePath );

        if ( configFile.exists() ) {
            configFile.delete();
        }

        configFile.createNewFile();

        DataOutputStream outputStream = null;

        try {

            outputStream = new DataOutputStream( new FileOutputStream( configFile ) );

            // config xml: http://help.adobe.com/en_US/flex/using/WS2db454920e96a9e51e63e3d11c0bf69084-7ac6.html
            outputStream.writeChars( "<?xml version=\"1.0\"?>\n<flex-config>\n" );

            String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();
            String generatedResourcesDirectoryPath = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
            for (File file : TargetDirectoryService.getResourceFiles()) {
                outputStream.writeChars( "<include-file><name>" );
                outputStream.writeChars( FileUtils.getCanonicalPath( file.getPath(), generatedResourcesDirectoryPath ) );
                outputStream.writeChars( "</name><path>" );
                outputStream.writeChars( FileUtils.getCanonicalPath( file.getPath(), targetDirectoryPath ) );
                outputStream.writeChars( "</path></include-file>\n" );
            }

            outputStream.writeChars( "</flex-config>" );
        }
        finally {
            IOUtil.close( outputStream );
        }
    }
}
