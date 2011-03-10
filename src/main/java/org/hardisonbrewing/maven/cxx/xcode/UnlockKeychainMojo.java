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

package org.hardisonbrewing.maven.cxx.xcode;

import java.util.LinkedList;
import java.util.List;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.PropertiesService;

/**
 * @goal xcode-unlock-keychain
 * @phase validate
 */
public final class UnlockKeychainMojo extends JoJoMojoImpl {

    private String keychain;
    private String keychainPassword;

    @Override
    public final void execute() {

        keychain = PropertiesService.getProperty( "xcode.keychain" );
        keychainPassword = PropertiesService.getProperty( "xcode.keychain.password" );

        if ( keychainPassword == null ) {
            getLog().info( "No keychain password specified... skipping" );
            return;
        }

        if ( keychain != null ) {
            addKeychain();
        }

        unlockKeychain();

        listKeychains();
    }

    private final void unlockKeychain() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "unlock-keychain" );
        cmd.add( "-p" );
        cmd.add( keychainPassword );

        if ( keychain != null ) {
            cmd.add( keychain );
        }
        else {
            getLog().info( "No keychain specified... using default" );
        }

        execute( cmd );
    }

    private final void addKeychain() {

        //        List<String> cmd = new LinkedList<String>();
        //        cmd.add( "security" );
        //        cmd.add( "default-keychain" );
        //        cmd.add( "-s" );
        //        cmd.add( keychain );
        //        execute( cmd );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "list-keychains" );
        cmd.add( "-s" );
        cmd.add( keychain );
        execute( cmd );
    }

    private final void listKeychains() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "list-keychains" );
        execute( cmd );
    }
}
