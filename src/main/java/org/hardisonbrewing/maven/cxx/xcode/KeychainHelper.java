package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.PropertiesService;

public class KeychainHelper {

    public static String findKeychainPath( Keychain keychain ) {

        if ( keychain.keychain.startsWith( File.separator ) ) {

            return keychain.keychain;
        }
        else if ( keychain.keychain.startsWith( "~/" ) ) {

            Properties properties = PropertiesService.getProperties();
            String userHome = properties.getProperty( "user.home" );
            return userHome + keychain.keychain.substring( 1 );
        }

        StringBuffer pathBuffer = new StringBuffer();
        pathBuffer.append( File.separator );
        pathBuffer.append( "Library" );
        pathBuffer.append( File.separator );
        pathBuffer.append( "Keychains" );
        pathBuffer.append( File.separator );
        pathBuffer.append( keychain.keychain );
        String path = pathBuffer.toString();

        if ( FileUtils.exists( path ) ) {
            return path;
        }

        Properties properties = PropertiesService.getProperties();
        String userHome = properties.getProperty( "user.home" );
        return userHome + path;
    }

    protected static final List<String> unlockKeychainCommand( Keychain keychain ) {

        List<String> cmd = new LinkedList<String>();
        if ( keychain != null ) {

            JoJoMojo.getMojo().getLog().debug( "Unlocking " + keychain.keychain );
            cmd.add( "security" );
            cmd.add( "unlock-keychain" );
            cmd.add( "-p" );
            cmd.add( keychain.password );
            if ( keychain.keychain != null ) {

                KeychainHelper.findKeychainPath( keychain );
            }
        }
        return cmd;
    }
}
