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
package org.hardisonbrewing.maven.cxx.qnx;

import java.io.File;
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.qde.CProjectService;
import org.hardisonbrewing.maven.cxx.qde.PropertiesService;

public final class QnxService {

    public static final String PACKAGING_QNX = "qnx";
    public static final String PACKAGING_QDE = "qde";

    public static final String QNX_USR_SEARCH;

    static {
        StringBuffer qnxUsrSearch = new StringBuffer();
        qnxUsrSearch.append( "**" );
        qnxUsrSearch.append( File.separator );
        qnxUsrSearch.append( "usr" );
        QNX_USR_SEARCH = qnxUsrSearch.toString();
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

    public static boolean isMakefileBuilder( String target ) {

        if ( CProjectService.getCProject() != null ) {
            return CProjectService.isMakefileBuilder( target );
        }

        return PACKAGING_QNX.equals( JoJoMojo.getMojo().getProject().getPackaging() );
    }

    public static boolean hasMakefile() {

        File file = getMakefile();
        return file.exists();
    }

    public static File getMakefile() {

        return new File( getMakefilePath() );
    }

    public static String getMakefilePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "Makefile" );
        return stringBuffer.toString();
    }

    public static String convertMakefileDirectoryToPlatform( File dir ) {

        String endian = null;
        String version = null;
        boolean debug = false;
        boolean staticLib = false;
        boolean application = false;

        for (String variant : dir.getName().split( "\\." )) {

            if ( "le".equals( variant ) ) {
                endian = "le";
            }
            else if ( "be".equals( variant ) ) {
                endian = "be";
            }
            else if ( "g".equals( variant ) ) {
                debug = true;
            }
            else if ( "a".equals( variant ) ) {
                staticLib = true;
            }
            else if ( "o".equals( variant ) ) {
                application = true;
            }
            else if ( variant.startsWith( "v" ) ) {
                version = variant;
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( dir.getParentFile().getName() );
        if ( endian != null ) {
            stringBuffer.append( endian );
        }
        if ( version != null ) {
            stringBuffer.append( "-" );
            stringBuffer.append( version );
        }
        return stringBuffer.toString();
    }
}
