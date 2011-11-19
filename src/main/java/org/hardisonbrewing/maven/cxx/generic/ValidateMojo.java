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

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    @Override
    public final void execute() {

        String[] resourceFilePaths = TargetDirectoryService.getResourceFilePaths();
        for (int i = 0; resourceFilePaths != null && i < resourceFilePaths.length; i++) {
            validate( resourceFilePaths[i] );
        }
    }

    public static final void validate( String fileName ) {

        if ( fileName.startsWith( FileUtils.PARENT_DIRECTORY_MARKER ) ) {
            throw new IllegalArgumentException( "File[" + fileName + "] is outside the project domain." );
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
}
