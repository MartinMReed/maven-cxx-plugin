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
package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.PropertiesService;

/**
 * @goal xcode-unlock-keychain
 * @phase generate-resources
 */
public class UnlockKeychainMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public Keychain keychain;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( keychain == null ) {
            return;
        }

        if ( keychain.keychain != null ) {

            String path = findKeychainPath( keychain.keychain );
            boolean exists = FileUtils.exists( path );

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "Using keychain path `" );
            stringBuffer.append( path );
            stringBuffer.append( "`... exists: " );
            stringBuffer.append( exists );
            getLog().info( stringBuffer.toString() );

            if ( !exists ) {
                createKeychain( keychain, path );
            }

            openKeychain( path );
        }

        unlockKeychain( keychain );
    }

    private void createKeychain( Keychain keychain, String path ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "create-keychain" );
        cmd.add( "-p" );
        cmd.add( keychain.password );
        cmd.add( path );
        execute( cmd );
    }

    private void openKeychain( String path ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "open" );
        cmd.add( path );
        execute( cmd );
    }

    private void unlockKeychain( Keychain keychain ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "unlock-keychain" );
        cmd.add( "-p" );
        cmd.add( keychain.password );
        if ( keychain.keychain != null ) {
            cmd.add( keychain.keychain );
        }
        execute( cmd );
    }

    private String findKeychainPath( String keychain ) {

        if ( keychain.startsWith( File.separator ) ) {
            return keychain;
        }

        StringBuffer pathBuffer = new StringBuffer();
        pathBuffer.append( File.separator );
        pathBuffer.append( "Library" );
        pathBuffer.append( File.separator );
        pathBuffer.append( "Keychains" );
        pathBuffer.append( File.separator );
        pathBuffer.append( keychain );
        String path = pathBuffer.toString();

        if ( FileUtils.exists( path ) ) {
            return path;
        }

        Properties properties = PropertiesService.getProperties();
        String userHome = properties.getProperty( "user.home" );
        return userHome + path;
    }
}
