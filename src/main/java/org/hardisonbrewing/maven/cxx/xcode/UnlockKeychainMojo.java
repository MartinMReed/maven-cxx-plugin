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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

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

        listKeychains();
        defaultKeychain( keychain );
        unlockKeychain( keychain );
    }

    private void listKeychains() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "-v" );
        cmd.add( "list-keychains" );
        execute( cmd );
    }

    private void defaultKeychain( Keychain keychain ) {

        if ( keychain.keychain == null ) {
            return;
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "-v" );
        cmd.add( "default-keychain" );
        cmd.add( "-s" );
        cmd.add( keychain.keychain );
        execute( cmd );
    }

    private void unlockKeychain( Keychain keychain ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "-v" );
        cmd.add( "unlock-keychain" );
        cmd.add( "-p" );
        cmd.add( keychain.password );
        if ( keychain.keychain != null ) {
            cmd.add( keychain.keychain );
        }
        execute( cmd );
    }
}
