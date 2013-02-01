/**
 * Copyright (c) 2010-2013 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;

/**
 * @goal xcode-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            List<String> cmd = buildCommand( scheme, true );
            Properties buildSettings = getBuildSettings( cmd );
            PropertiesService.storeBuildSettings( buildSettings, scheme );
            execute( cmd );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                List<String> cmd = buildCommand( target, false );
                Properties buildSettings = getBuildSettings( cmd );
                PropertiesService.storeBuildSettings( buildSettings, target );
                execute( cmd );
            }
        }
    }

    private List<String> buildCommand( String buildTarget, boolean scheme ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcodebuild" );

        String workspacePath = XCodeService.getXcworkspacePath();

        if ( workspacePath != null ) {
            cmd.add( "-workspace" );
            cmd.add( workspacePath );
        }
        else {
            cmd.add( "-project" );
            cmd.add( XCodeService.getXcprojPath() );
        }

        if ( scheme ) {
            cmd.add( "-scheme" );
        }
        else {
            cmd.add( "-target" );
        }
        cmd.add( buildTarget );

        String configuration = XCodeService.getConfiguration( buildTarget );
        cmd.add( "-configuration" );
        cmd.add( configuration );

        // TODO Determine if we should Archive or Build (Just build for now)
        cmd.add( XCodeService.ACTION_BUILD );

        StringBuffer symroot = new StringBuffer();
        symroot.append( TargetDirectoryService.getTargetBuildDirPath( buildTarget ) );
        symroot.append( File.separator );
        symroot.append( "Build" );
        cmd.add( "SYMROOT=" + symroot.toString() );

        cmd.add( "BUILD_DIR=$(SYMROOT)" );
        cmd.add( "CONFIGURATION_BUILD_DIR=$(BUILD_DIR)" + File.separator + "Products" );
        cmd.add( "OBJROOT=$(BUILD_DIR)" + File.separator + "Intermediates" );

        cmd.add( "CONFIGURATION=" + configuration );

        String codeSignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codeSignIdentity != null ) {
            cmd.add( "CODE_SIGN_IDENTITY=" + codeSignIdentity );
        }

        return cmd;
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

    private Properties getBuildSettings( List<String> cmd ) {

        cmd = new LinkedList<String>( cmd );
        cmd.add( "-showBuildSettings" );

        Properties properties = new Properties();
        PropertyStreamConsumer streamConsumer = new PropertyStreamConsumer( properties );
        execute( cmd, streamConsumer, null );

        return properties;
    }

    private static class PropertyStreamConsumer implements StreamConsumer {

        private final Properties properties;

        public PropertyStreamConsumer(Properties properties) {

            this.properties = properties;
        }

        @Override
        public void consumeLine( String line ) {

            line = line.trim();

            int indexOf = line.indexOf( '=' );

            if ( indexOf == -1 ) {
                return;
            }

            String key = line.substring( 0, indexOf ).trim();
            String value = line.substring( indexOf + 1 ).trim();
            properties.put( key, value );
        }
    }
}
