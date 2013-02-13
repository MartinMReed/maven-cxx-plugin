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
package org.hardisonbrewing.maven.cxx.generic;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.PropertiesService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public Source[] sources;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        String[] resourceFilePaths = TargetDirectoryService.getResourceFilePaths();
        for (int i = 0; resourceFilePaths != null && i < resourceFilePaths.length; i++) {
            validateInsideProject( resourceFilePaths[i] );
        }

        if ( sources != null ) {

            boolean defaultDirectoryUsed = false;

            for (Source source : sources) {

                if ( source.directory != null ) {
                    validateInsideProject( source.directory );
                    continue;
                }

                if ( defaultDirectoryUsed ) {
                    JoJoMojo.getMojo().getLog().error( "Default directory used for multiple <source/> entries." );
                    throw new IllegalArgumentException();
                }

                defaultDirectoryUsed = true;
            }
        }
    }

    private static final void validateInsideProject( String filename ) {

        if ( filename.startsWith( FileUtils.PARENT_DIRECTORY_MARKER ) ) {
            JoJoMojo.getMojo().getLog().error( "File[" + filename + "] is outside the project domain." );
            throw new IllegalArgumentException();
        }
    }

    public static final void checkPropertyExists( String key, boolean force ) {

        StringBuffer checkingMessage = new StringBuffer();
        checkingMessage.append( "Checking property `" );
        checkingMessage.append( key );
        checkingMessage.append( "`" );
        if ( !force ) {
            checkingMessage.append( " (optional)" );
        }
        checkingMessage.append( "..." );
        JoJoMojo.getMojo().getLog().info( checkingMessage );

        String sdkHome = PropertiesService.getProperty( key );
        if ( sdkHome != null ) {
            return;
        }
        if ( force ) {
            JoJoMojo.getMojo().getLog().error( "Property `" + key + "` must be set!" );
            throw new IllegalStateException();
        }
        else {
            JoJoMojo.getMojo().getLog().warn( "Property `" + key + "` is not set." );
        }
    }

    public static final void checkConfigurationExists( String key, Object value, boolean force ) {

        if ( key.indexOf( '<' ) == -1 ) {
            key = "<" + key + "/>";
        }

        StringBuffer checkingMessage = new StringBuffer();
        checkingMessage.append( "Checking configuration " );
        checkingMessage.append( key );
        if ( !force ) {
            checkingMessage.append( " (optional)" );
        }
        checkingMessage.append( "..." );
        JoJoMojo.getMojo().getLog().info( checkingMessage );

        if ( value != null ) {
            if ( value instanceof String ) {
                String str = (String) value;
                if ( str.length() > 0 ) {
                    return;
                }
            }
            else {
                return;
            }
        }
        if ( force ) {
            JoJoMojo.getMojo().getLog().error( "Configuration " + key + " must be set!" );
            throw new IllegalStateException();
        }
        else {
            JoJoMojo.getMojo().getLog().warn( "Configuration " + key + " is not set." );
        }
    }
}
