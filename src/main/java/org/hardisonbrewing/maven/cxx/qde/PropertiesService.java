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
package org.hardisonbrewing.maven.cxx.qde;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.qnx.PropertiesService {

    private static final String WORKSPACE_PATH_REGEX = "\\$\\{workspace_loc:(.*)\\}";

    protected PropertiesService() {

        // do nothing
    }

    public static boolean isWorkspaceValue( String value ) {

        Pattern pattern = Pattern.compile( WORKSPACE_PATH_REGEX );
        Matcher matcher = pattern.matcher( value );
        return matcher.matches();
    }

    public static String getWorkspaceValue( String value ) {

        Pattern pattern = Pattern.compile( WORKSPACE_PATH_REGEX );
        Matcher matcher = pattern.matcher( value );

        if ( !matcher.matches() ) {
            return null;
        }

        return matcher.group( 1 );
    }

    public static String getWorkspacePath( String value ) {

        String filePath = getWorkspaceValue( value );

        if ( filePath == null ) {
            return filePath;
        }

        File baseDir = ProjectService.getBaseDir();
        String baseDirName = baseDir.getName();
        int indexOf = filePath.indexOf( baseDirName );

        if ( indexOf == -1 ) {
            JoJoMojo.getMojo().getLog().warn( "Unable to locate target build directory to clean up... bailing" );
            return null;
        }

        indexOf += baseDirName.length();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( baseDir.getPath() );
        if ( filePath.charAt( indexOf ) != File.separatorChar ) {
            stringBuffer.append( File.separator );
        }
        stringBuffer.append( filePath.substring( indexOf ) );
        return stringBuffer.toString();
    }

    public static File getWorkspaceFile( String value ) {

        return new File( getWorkspacePath( value ) );
    }
}
