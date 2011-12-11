/**
 * Copyright (c) 2011 Martin M Reed
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
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.cxx.ProjectService;

public final class QdeService {

    public static final String QNX_USR_SEARCH;

    static {
        StringBuffer qnxUsrSearch = new StringBuffer();
        qnxUsrSearch.append( "**" );
        qnxUsrSearch.append( File.separator );
        qnxUsrSearch.append( "usr" );
        QNX_USR_SEARCH = qnxUsrSearch.toString();
    }

    public static File getCProjectFile() {

        return new File( getCProjectFilePath() );
    }

    public static String getCProjectFilePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( CProjectService.CPROJECT_FILENAME );
        return stringBuffer.toString();
    }

    public static String getQnxTargetDirPath() {

        File qnxTargetDir = PropertiesService.getPropertyAsFile( PropertiesService.ENV_QNX_TARGET );
        if ( qnxTargetDir != null && qnxTargetDir.exists() ) {
            return qnxTargetDir.getPath();
        }

        StringBuffer qnxTargetBaseDirPath = new StringBuffer();
        qnxTargetBaseDirPath.append( PropertiesService.getProperty( PropertiesService.BLACKBERRY_NDK_HOME ) );
        qnxTargetBaseDirPath.append( File.separator );
        qnxTargetBaseDirPath.append( "target" );

        StringBuffer qnxTargetDirPath = new StringBuffer();
        qnxTargetDirPath.append( qnxTargetBaseDirPath );
        qnxTargetDirPath.append( File.separator );
        qnxTargetDirPath.append( getQnxTargetDirName( qnxTargetBaseDirPath.toString() ) );
        return qnxTargetDirPath.toString();
    }

    private static String getQnxTargetDirName( String fileDirPath ) {

        File file = new File( fileDirPath );

        for (String filename : file.list()) {
            if ( filename.startsWith( "qnx" ) ) {
                return filename;
            }
        }

        return null;
    }

    public static String getQnxHostDirPath() {

        File qnxHostDir = PropertiesService.getPropertyAsFile( PropertiesService.ENV_QNX_HOST );
        if ( qnxHostDir != null && qnxHostDir.exists() ) {
            return qnxHostDir.getPath();
        }

        File qnxHostUsrDir = new File( getQnxHostUsrDirPath() );
        return qnxHostUsrDir.getParent();
    }

    public static String getQnxCompilerDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getQnxHostDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "etc" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "qcc" );
        return stringBuffer.toString();
    }

    public static String getDefaultCompiler() {

        Properties properties = PropertiesService.getDefaultCompilerProperties();
        return properties.getProperty( "DIR" );
    }

    public static String getDefaultCompilerVersion( String compiler ) {

        Properties properties = PropertiesService.getDefaultCompilerVersionProperties( compiler );
        return properties.getProperty( "DIR" );
    }

    public static String getQnxHostUsrDirPath() {

        File qnxHostDir = PropertiesService.getPropertyAsFile( PropertiesService.ENV_QNX_HOST );
        if ( qnxHostDir != null && qnxHostDir.exists() ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( qnxHostDir.getPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( "usr" );
            return stringBuffer.toString();
        }

        StringBuffer qnxHostBaseDirPath = new StringBuffer();
        qnxHostBaseDirPath.append( PropertiesService.getProperty( PropertiesService.BLACKBERRY_NDK_HOME ) );
        qnxHostBaseDirPath.append( File.separator );
        qnxHostBaseDirPath.append( "host" );

        File qnxHostBaseDir = new File( qnxHostBaseDirPath.toString() );

        String[] includes = new String[] { QNX_USR_SEARCH };
        String[] files = FileUtils.listDirectoryPathsRecursive( qnxHostBaseDir, includes, null );

        if ( files.length > 0 ) {
            return files[0];
        }

        return null;
    }

    public static final String getEclipseDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getQnxHostUsrDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "qde" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "eclipse" );
        return stringBuffer.toString();
    }
}
