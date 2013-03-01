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
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.generic.ValidationService;

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

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        validateOS();

        validateOcunit2JunitPath();

        if ( keychain != null ) {
            ValidationService.assertConfigurationExists( "<keychain><password/></keychain>", keychain.password );
        }

        File workspace = XCodeService.loadWorkspace();

        if ( workspace != null ) {
            ValidationService.assertConfigurationExists( "scheme", scheme );
            validateWorkspace();
        }
        else {
            File project = XCodeService.loadProject();
            validateProject( project );
        }

        if ( scheme != null ) {
            XCodeService.loadSchemes();
            validateScheme( scheme );
        }
    }

    private void validateOS() {

        Properties properties = PropertiesService.getProperties();
        String osName = properties.getProperty( "os.name" );

        if ( !"Mac OS X".equals( osName ) ) {
            getLog().error( "Unsupported OS: " + osName );
            throw new IllegalStateException();
        }
    }

    private void validateProject( File project ) {

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

        if ( targetIncludes != null || targetExcludes != null ) {
            if ( targetIncludes.length > 0 || targetExcludes.length > 0 ) {
                JoJoMojo.getMojo().getLog().error( "Invalid workspace configuration! The pom.xml must not specify any targets in `targetIncludes` or `targetExcludes`." );
                throw new IllegalStateException();
            }
        }
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
            stringBuffer.append( "\r\n  " );
            stringBuffer.append( _scheme );
        }
        getLog().error( stringBuffer.toString() );
        throw new IllegalStateException();
    }

    private void validateOcunit2JunitPath() {

        String path = PropertiesService.getProperty( PropertiesService.OCUNIT_2_JUNIT_HOME );

        if ( path != null && !FileUtils.fileExists( path ) ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "File not found for property `" );
            stringBuffer.append( PropertiesService.OCUNIT_2_JUNIT_HOME );
            stringBuffer.append( "`: " );
            stringBuffer.append( path );
            getLog().error( stringBuffer.toString() );
            throw new IllegalStateException();
        }
    }
}
