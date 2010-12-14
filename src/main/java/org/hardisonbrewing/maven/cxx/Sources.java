/**
 * Copyright (c) 2010 Martin M Reed
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

package org.hardisonbrewing.maven.cxx;

import java.io.File;

import org.hardisonbrewing.maven.core.TargetDirectoryService;

public class Sources {

    public static final String generateSource( String source, String extension ) {

        source = source.substring( 0, source.lastIndexOf( '.' ) );
        return escapeFileName( source + "." + extension );
    }

    public static final String escapeFileName( String filename ) {

        if ( filename.matches( "^[a-zA-z0-9/.]*$" ) ) {
            return filename;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "\"" );
        stringBuffer.append( TargetDirectoryService.getTargetDirectory() );
        stringBuffer.append( File.separator );
        stringBuffer.append( filename );
        stringBuffer.append( "\"" );
        return stringBuffer.toString();
    }
}
