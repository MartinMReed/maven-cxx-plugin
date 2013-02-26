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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TemplateService;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;
import org.hardisonbrewing.maven.cxx.xcode.TargetDirectoryService;

/**
 * @goal msbuild-generate-build-settings
 * @phase compile
 */
public final class GenerateBuildSettingsMojo extends JoJoMojoImpl {

    private static final String ENVVAR_MARKER = "envvar::";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File buildSettingsFile = new File( getBuildSettingsPath() );

        generateTargetFile( buildSettingsFile );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "MSBuild" );
        cmd.add( buildSettingsFile.getPath() );
        cmd.add( "/t:Environment" );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );

        Properties properties = new Properties();
        StreamConsumer systemOut = new PropertyStreamConsumer( properties, LogStreamConsumer.LEVEL_INFO );
        StreamConsumer systemErr = new LogStreamConsumer( LogStreamConsumer.LEVEL_ERROR );
        execute( commandLine, systemOut, systemErr );

        PropertiesService.storeBuildSettings( properties );
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

    private String getBuildSettingsPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "buildSettings.xml" );
        return stringBuffer.toString();
    }

    private final void generateTargetFile( File file ) {

        List<String> properties = new LinkedList<String>();
        properties.add( MSBuildService.BUILD_XAP_FILENAME );
        properties.add( MSBuildService.BUILD_CONFIGURATION );
        properties.add( MSBuildService.BUILD_XAP_OUTPUTS );
        properties.add( MSBuildService.BUILD_ASSEMBLY_NAME );

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "project", MSBuildService.getProjectFilePath() );
        velocityContext.put( "envvar_marker", ENVVAR_MARKER );
        velocityContext.put( "properties", properties );

        if ( !file.getParentFile().exists() ) {
            FileUtils.ensureParentExists( file.getPath() );
        }

        getLog().info( "Generating " + file + "..." );

        Template template = TemplateService.getTemplateFromClasspath( "/msbuild/buildSettings.vm" );

        try {
            TemplateService.writeTemplate( template, velocityContext, file );
        }
        catch (IOException e) {
            throw new IllegalStateException( e );
        }
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
