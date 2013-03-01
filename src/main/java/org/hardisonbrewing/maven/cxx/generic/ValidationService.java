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
package org.hardisonbrewing.maven.cxx.generic;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.PropertiesService;

public final class ValidationService {

    public static final void validateInsideProject( String filename ) {

        if ( filename.startsWith( FileUtils.PARENT_DIRECTORY_MARKER ) ) {
            JoJoMojo.getMojo().getLog().error( "File[" + filename + "] is outside the project domain." );
            throw new IllegalArgumentException();
        }
    }

    public static final void checkPropertyExists( String key ) {

        String property = PropertiesService.getProperty( key );
        if ( property != null ) {
            return;
        }

        JoJoMojo.getMojo().getLog().error( "Property `" + key + "` must be set!" );
        throw new IllegalStateException();
    }

    public static final void assertConfigurationExists( String key, Object value ) {

        if ( value != null ) {

            if ( !( value instanceof String ) ) {
                return;
            }

            String str = (String) value;
            if ( str.length() > 0 ) {
                return;
            }
        }

        if ( key.indexOf( '<' ) == -1 ) {
            key = "<" + key + "/>";
        }

        JoJoMojo.getMojo().getLog().error( "Configuration " + key + " must be set!" );
        throw new IllegalStateException();
    }
}
