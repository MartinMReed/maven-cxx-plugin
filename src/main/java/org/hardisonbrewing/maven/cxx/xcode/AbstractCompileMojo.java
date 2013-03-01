/**
 * Copyright (c) 2010-2013 Martin M Reed
 * Copyright (c) 2013 Todd Grooms
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;

public abstract class AbstractCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String action;

    /**
     * @parameter
     */
    public String scheme;

    /**
     * @parameter
     */
    public String sdk;

    /**
     * @parameter  default-value="${maven.test.skip}"
     */
    public boolean skipTests;

    protected List<String> buildCommand( String target ) {

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

        if ( scheme != null ) {
            cmd.add( "-scheme" );
        }
        else {
            cmd.add( "-target" );
        }
        cmd.add( target );

        String configuration = XCodeService.getConfiguration( target );
        cmd.add( "-configuration" );
        cmd.add( configuration );

        if ( sdk != null ) {
            cmd.add( "-sdk" );
            cmd.add( sdk );
        }

        if ( action == null ) {
            cmd.add( XCodeService.ACTION_BUILD );
        }
        else {
            cmd.add( action );
        }

        if ( scheme == null ) {

            StringBuffer symroot = new StringBuffer();
            symroot.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
            symroot.append( File.separator );
            symroot.append( "Build" );
            cmd.add( "SYMROOT=" + symroot.toString() );

            cmd.add( "BUILD_DIR=$(SYMROOT)" );
            cmd.add( "CONFIGURATION_BUILD_DIR=$(BUILD_DIR)" + File.separator + "Products" );
            cmd.add( "OBJROOT=$(BUILD_DIR)" + File.separator + "Intermediates" );
        }

        cmd.add( "CONFIGURATION=" + configuration );

        String codeSignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codeSignIdentity != null ) {
            cmd.add( "CODE_SIGN_IDENTITY=" + codeSignIdentity );
        }
        else {
            JoJoMojo.getMojo().getLog().info( "No codesign identity found. Disabling signing..." );
            cmd.add( "CODE_SIGN_IDENTITY=" );
            cmd.add( "CODE_SIGNING_REQUIRED=NO" );
        }

        if ( !skipTests ) {
            cmd.add( "TEST_AFTER_BUILD=YES" );
            cmd.add( "RUN_UNIT_TEST_WITH_IOS_SIM=YES" );
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

    protected Properties loadBuildSettings( List<String> cmd ) {

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

    protected static class LogCopyStreamConsumer extends LogStreamConsumer {

        private final OutputStream outputStream;

        public LogCopyStreamConsumer(OutputStream outputStream, int level) {

            super( level );

            this.outputStream = outputStream;
        }

        @Override
        public void consumeLine( String line ) {

            super.consumeLine( line );

            try {
                outputStream.write( line.getBytes() );
                outputStream.write( "\r\n".getBytes() );
            }
            catch (IOException e) {
                throw new IllegalStateException( e );
            }
        }
    }
}
