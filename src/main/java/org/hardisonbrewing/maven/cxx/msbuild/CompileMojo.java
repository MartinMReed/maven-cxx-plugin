/**
 * Copyright (c) 2013 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.msbuild;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;

/**
 * @goal msbuild-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    private static final String ENVVAR_MARKER = "envvar::";
    private static final String BUILD_SETTINGS_TARGET = "Environment";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String project = MSBuildService.getProject();
        storeBuildSettings( project );
        compile( project );
    }

    @Override
    protected Commandline buildCommandline( List<String> cmd ) {

        try {
            return CommandLineService.build( cmd );
        }
        catch (CommandLineException e) {
            throw new IllegalStateException( e );
        }
    }

    private void storeBuildSettings( String project ) {

        List<String> variables = new LinkedList<String>();
        variables.add( MSBuildService.BUILD_XAP_FILENAME );
        variables.add( MSBuildService.BUILD_CONFIGURATION );
        variables.add( MSBuildService.BUILD_XAP_OUTPUTS );
        variables.add( MSBuildService.BUILD_ASSEMBLY_NAME );

        File buildSettingsTarget = new File( getBuildSettingsTargetPath() );

        try {
            writeEnvironmentTarget( buildSettingsTarget, project, variables );
        }
        catch (Exception e) {
            getLog().error( "Unable to write build settings target file: " + buildSettingsTarget );
            throw new IllegalStateException();
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "MSBuild" );
        cmd.add( buildSettingsTarget.getPath() );
        cmd.add( "/t:" + BUILD_SETTINGS_TARGET );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );

        Properties properties = new Properties();
        StreamConsumer systemOut = new PropertyStreamConsumer( properties, LogStreamConsumer.LEVEL_INFO );
        StreamConsumer systemErr = new LogStreamConsumer( LogStreamConsumer.LEVEL_ERROR );
        execute( commandLine, systemOut, systemErr );

        PropertiesService.storeBuildSettings( properties );
    }

    private String getBuildSettingsTargetPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "buildSettings.xml" );
        return stringBuffer.toString();
    }

    private void writeEnvironmentTarget( File file, String project, List<String> properties ) throws Exception {

        String environmentTarget = buildEnvironmentTarget( project, properties );

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream( file );
            outputStream.write( environmentTarget.getBytes() );
        }
        finally {
            IOUtil.close( outputStream );
        }
    }

    private String buildEnvironmentTarget( String project, List<String> properties ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "<Project xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\">\r\n" );
        stringBuffer.append( "\t<Import Project=\"" );
        stringBuffer.append( project );
        stringBuffer.append( "\" />\r\n\t<Target Name=\"Environment\">\r\n" );
        for (String property : properties) {
            stringBuffer.append( "\t\t<Message Text=\"" );
            stringBuffer.append( ENVVAR_MARKER );
            stringBuffer.append( property );
            stringBuffer.append( "=$(" );
            stringBuffer.append( property );
            stringBuffer.append( ")\" />\r\n" );
        }
        stringBuffer.append( "\t</Target>\r\n</Project>" );
        return stringBuffer.toString();
    }

    private void compile( String project ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "MSBuild" );
        cmd.add( project );

        cmd.add( "/p:OutDir=" + TargetDirectoryService.getBinDirectoryPath() );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );
        execute( commandLine );
    }

    private static class PropertyStreamConsumer extends LogStreamConsumer {

        private final Properties properties;

        public PropertyStreamConsumer(Properties properties, int level) {

            super( level );

            this.properties = properties;
        }

        @Override
        public void consumeLine( String line ) {

            line = line.trim();

            int marker = line.indexOf( ENVVAR_MARKER );
            if ( marker == -1 ) {
                super.consumeLine( line );
                return;
            }

            int indexOf = line.indexOf( '=' );
            if ( indexOf == -1 ) {
                super.consumeLine( line );
                return;
            }

            String key = line.substring( marker + ENVVAR_MARKER.length(), indexOf ).trim();
            String value = line.substring( indexOf + 1 ).trim();
            properties.put( key, value );
        }
    }
}
