package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
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
}
