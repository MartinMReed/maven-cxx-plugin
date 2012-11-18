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

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.PropertiesService;

/**
 * @goal xcode-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String[] targetIncludes;

    /**
     * @parameter
     */
    public String[] targetExcludes;

    /**
     * @parameter
     */
    public String scheme;
    /**
     * @parameter
     */
    public Keychain keychain;
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

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        validateOS();

        File workspace = XCodeService.loadWorkspace();
        if ( workspace != null ) {
            validateWorkspace();
        }
        else {
            validateProject();
        }

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "provisioningProfile", provisioningProfile, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "configuration", configuration, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "codesignCertificate", codesignCertificate, false );

        String keychainPassword = keychain == null ? null : keychain.password;
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "keychain", keychain, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "<keychain><password/></keychain>", keychainPassword, false );

        if ( keychain != null ) {
            validateKeychain( keychain );
        }
    }

    private void validateKeychain( Keychain _keychain ) {

        String keychain = _keychain.keychain;

        if ( keychain.startsWith( File.separator ) ) {
            getLog().info( "Searching for keychain `" + keychain + "`..." );
            if ( FileUtils.exists( keychain ) ) {
                return;
            }
        }
        else {

            StringBuffer pathBuffer = new StringBuffer();
            pathBuffer.append( File.separator );
            pathBuffer.append( "Library" );
            pathBuffer.append( File.separator );
            pathBuffer.append( "Keychains" );
            pathBuffer.append( File.separator );
            pathBuffer.append( keychain );
            String path = pathBuffer.toString();

            Properties properties = PropertiesService.getProperties();
            String userPath = properties.getProperty( "user.home" ) + path;

            getLog().info( "Searching for keychain `" + keychain + "` at `" + userPath + "`..." );
            if ( FileUtils.exists( userPath ) ) {
                return;
            }

            getLog().info( "Searching for keychain `" + keychain + "` at `" + path + "`..." );
            if ( FileUtils.exists( path ) ) {
                return;
            }
        }

        getLog().error( "Unable to locate keychain `" + keychain + "`." );
        throw new IllegalStateException();
    }

    private void validateOS() {

        Properties properties = PropertiesService.getProperties();
        String osName = properties.getProperty( "os.name" );
        if ( !"Mac OS X".equals( osName ) ) {
            getLog().error( "Unsupported OS: " + osName );
            throw new IllegalStateException();
        }
    }

    private void validateProject() {

        File project = XCodeService.loadProject();

        if ( project == null ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate project entry! Expected a file with the extension `" + XCodeService.XCODEPROJ_EXTENSION + "`." );
            throw new IllegalStateException();
        }

        if ( targetIncludes != null && targetExcludes != null ) {
            if ( targetIncludes.length > 0 && targetExcludes.length > 0 ) {
                JoJoMojo.getMojo().getLog().error( "Invalid target configuration! The pom.xml must not specify both `targetIncludes` and `targetExcludes`." );
                throw new IllegalStateException();
            }
        }
    }

    private void validateWorkspace() {

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "scheme", scheme, true );

        if ( targetIncludes != null || targetExcludes != null ) {
            if ( targetIncludes.length > 0 || targetExcludes.length > 0 ) {
                JoJoMojo.getMojo().getLog().error( "Invalid workspace configuration! The pom.xml must not specify any targets in `targetIncludes` or `targetExcludes`." );
                throw new IllegalStateException();
            }
        }

        XCodeService.loadSchemes();
        validateScheme( scheme );
    }

    private void validateScheme( String scheme ) {

        String[] schemes = XCodeService.getSchemes();

        if ( schemes == null ) {
            getLog().error( "Unable to load available schemes for the workspace." );
            throw new IllegalStateException();
        }

        for (String _scheme : schemes) {
            if ( scheme.equals( _scheme ) ) {
                return;
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Invalid scheme! `" );
        stringBuffer.append( scheme );
        stringBuffer.append( "` was not found. Available options are:" );
        for (String _scheme : schemes) {
            stringBuffer.append( "\n  " );
            stringBuffer.append( _scheme );
        }
        getLog().error( stringBuffer.toString() );
        throw new IllegalStateException();
    }
}
