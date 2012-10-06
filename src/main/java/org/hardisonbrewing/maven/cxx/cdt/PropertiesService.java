/**
 * Copyright (c) 2010-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.cdt;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.PropertiesService {

    private static final String WORKSPACE_PATH_REGEX = "\\$\\{workspace_loc:(.*)\\}";
    private static final String WORKSPACE_PROJ_NAME = "${ProjName}";

    protected PropertiesService() {

        // do nothing
    }

    public static boolean isWorkspaceValue( String value ) {

        Pattern pattern = Pattern.compile( WORKSPACE_PATH_REGEX );
        Matcher matcher = pattern.matcher( value );
        return matcher.matches();
    }

    public static String getWorkspaceValue( Configuration configuration, String value ) {

        Pattern pattern = Pattern.compile( WORKSPACE_PATH_REGEX );
        Matcher matcher = pattern.matcher( value );

        if ( !matcher.matches() ) {
            return null;
        }

        value = matcher.group( 1 );

        // we may not even be in a workspace, so ignore these
        value = trimStart( value, File.separator + WORKSPACE_PROJ_NAME );
        value = trimStart( value, File.separator + configuration.getArtifactName() );

        return ProjectService.getBaseDirPath() + value;
    }

    private static String trimStart( String str, String find ) {

        if ( str.startsWith( find ) ) {

            return str.substring( find.length() );
        }

        return str;
    }

    public static String getWorkspacePath( Configuration configuration, String value ) {

        String filePath = getWorkspaceValue( configuration, value );

        if ( filePath == null ) {
            return filePath;
        }

        File baseDir = ProjectService.getBaseDir();
        String baseDirName = baseDir.getName();
        int indexOf = filePath.indexOf( baseDirName );

        if ( indexOf == -1 ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "Unable to determine target build path from: `" );
            stringBuffer.append( value );
            stringBuffer.append( "`" );
            JoJoMojo.getMojo().getLog().error( stringBuffer.toString() );
            throw new IllegalStateException();
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

    public static File getWorkspaceFile( Configuration configuration, String value ) {

        return new File( getWorkspacePath( configuration, value ) );
    }
}
