/**
 * Copyright (c) 2010-2013 Martin M Reed
 * Copyright (c) 2012 Todd Grooms
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    /**
     * @parameter
     */
    public String configuration;

    /**
     * @parameter
     */
    public String codesignCertificate;

    /**
     * @parameter
     */
    public String scheme;

    /**
     * @parameter
     */
    public Keychain keychain;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        XCodeService.setConfiguration( configuration );

        File workspace = XCodeService.loadWorkspace();
        if ( workspace != null ) {

            getLog().debug( "Using workspace " + workspace );
            initWorkspace( workspace );
        }

        File project = XCodeService.loadProject();
        String projectPath = project.getPath();
        initProject( projectPath );

        if ( scheme != null ) {

            initScheme( scheme );
        }

        if ( provisioningProfile != null ) {

            ProvisioningProfileService.assertProvisioningProfile( provisioningProfile );
        }

        if ( codesignCertificate != null ) {

            CodesignCertificateService.assertCodesignCertificate( codesignCertificate );
        }

        if ( keychain != null ) {

            initKeychain( keychain );
        }
    }

    private void initWorkspace( File workspace ) {

        XCodeService.setXcworkspacePath( workspace.getPath() );

        String workspaceName = workspace.getName();
        workspaceName.substring( 0, workspaceName.lastIndexOf( XCodeService.XCWORKSPACE_EXTENSION ) - 1 );
        XCodeService.setWorkspace( workspaceName );
    }

    private void initProject( String projectPath ) {

        getLog().debug( "Initializing project with Path: " + projectPath );

        XCodeService.setXcprojPath( projectPath );

        int startIndex = projectPath.lastIndexOf( File.separatorChar );
        int endIndex = projectPath.lastIndexOf( XCodeService.XCODEPROJ_EXTENSION );
        String projectName = projectPath.substring( startIndex + 1, endIndex - 1 );
        XCodeService.setProject( projectName );
    }

    private void initScheme( String scheme ) {

        XCodeService.setScheme( scheme );

        Properties properties = PropertiesService.getXCodeProperties();
        properties.put( XCodeService.PROP_SCHEME, scheme );
        PropertiesService.storeXCodeProperties( properties );
    }

    private void initKeychain( Keychain keychain ) {

        String keychainPath;
        if ( keychain.keychain == null ) {
            keychainPath = defaultKeychain();
        }
        else {
            keychainPath = findKeychainPath( keychain.keychain );
        }
        getLog().info( "Using keychain: " + keychainPath );
        XCodeService.setKeychainPath( keychainPath );
    }

    private String defaultKeychain() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "security" );
        cmd.add( "default-keychain" );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, streamConsumer );

        Pattern pattern = Pattern.compile( UnlockKeychainMojo.KEYCHAIN_REGEX );
        Matcher matcher = pattern.matcher( streamConsumer.getOutput() );

        if ( matcher.find() ) {
            return matcher.group( 1 );
        }

        getLog().error( "Unable to locate default keychain. You must specify the keyhain name or path in the pom.xml." );
        throw new IllegalStateException();
    }

    private String findKeychainPath( String keychain ) {

        if ( keychain.startsWith( File.separator ) ) {
            return keychain;
        }
        else if ( keychain.startsWith( "~/" ) ) {
            Properties properties = PropertiesService.getProperties();
            String userHome = properties.getProperty( "user.home" );
            return userHome + keychain.substring( 1 );
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
