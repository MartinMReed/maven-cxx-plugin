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
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;

/**
 * @goal bar-launch
 * @execute phase="package"
 * @execute goal="bar-install"
 */
public final class LaunchMojo extends JoJoMojoImpl {

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if ( !shouldExecute() ) {
            getLog().info( "Unable to launch application!" );
            throw new IllegalStateException();
        }

        String artifactId = getProject().getArtifactId();

        List<String> cmd = new LinkedList<String>();
        cmd.add( "blackberry-deploy" );

        cmd.add( "-package" );
        cmd.add( artifactId + ".bar" );

        cmd.add( "-installApp" );

        cmd.add( "-launchApp" );

        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-devMode" );
            cmd.add( "-debugHost" );
            cmd.add( getIpAddress() );
        }

        String deviceIp = PropertiesService.getProperty( PropertiesService.BLACKBERRY_TABLET_DEVICE_IP );
        if ( deviceIp != null ) {
            cmd.add( "-device" );
            cmd.add( deviceIp );
        }

        String devicePassword = PropertiesService.getProperty( PropertiesService.BLACKBERRY_TABLET_DEVICE_PASSWORD );
        if ( devicePassword != null ) {
            cmd.add( "-password" );
            cmd.add( devicePassword );
        }

        Commandline commandLine = buildCommandline( cmd );

        String sdkHome = PropertiesService.getProperty( PropertiesService.BLACKBERRY_TABLET_HOME );
        if ( sdkHome != null ) {
            CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        }

        execute( commandLine );
    }

    public static String getIpAddress() {

        try {
            return getIpAddress( InetAddress.getLocalHost() );
        }
        catch (Exception e) {
            JoJoMojo.getMojo().getLog().error( "Unable to determine the local IP address for debugging!" );
            throw new IllegalStateException( e.getMessage() );
        }
    }

    private static String getIpAddress( InetAddress inetAddress ) {

        byte[] address = inetAddress.getAddress();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < address.length; i++) {
            if ( i > 0 ) {
                stringBuffer.append( "." );
            }
            stringBuffer.append( address[i] & 0xFF );
        }
        return stringBuffer.toString();
    }

    public static final boolean isIpAddressReachable( String hostname ) {

        if ( hostname == null ) {
            return false;
        }
        try {
            InetAddress inetAddress = InetAddress.getByName( hostname );
            return inetAddress.isReachable( 5000 );
        }
        catch (Exception e) {
            return false;
        }
    }

    private final boolean shouldExecute() {

        String deviceIp = PropertiesService.getProperty( PropertiesService.BLACKBERRY_TABLET_DEVICE_IP );
        if ( deviceIp == null ) {
            getLog().info( "Property `deviceIp` is not set." );
            return false;
        }

        boolean reachable = LaunchMojo.isIpAddressReachable( deviceIp );
        StringBuffer reachableMessage = new StringBuffer();
        reachableMessage.append( "Device IP[" );
        reachableMessage.append( deviceIp );
        reachableMessage.append( "] is " );
        if ( !reachable ) {
            reachableMessage.append( "not " );
        }
        reachableMessage.append( "reachable." );
        getLog().info( reachableMessage.toString() );
        return reachable;
    }
}
