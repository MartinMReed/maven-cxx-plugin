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

import generated.org.eclipse.cdt.Cproject;
import generated.org.eclipse.cdt.StorageModule.Configuration;
import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.io.File;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;

public final class QnxService {

    private static final String QCC_OPTION = "com.qnx.qcc.option";
    private static final String QCC_OPTION_CPU = QCC_OPTION + ".cpu";
    private static final String QCC_OPTION_GEN_CPU = QCC_OPTION + ".gen.cpu";

    private static final String QCC_OPTION_COMPILER = QCC_OPTION + ".compiler";
    private static final String QCC_OPTION_COMPILER_DEFINES = QCC_OPTION_COMPILER + ".defines";
    private static final String QCC_OPTION_COMPILER_INCLUDE_PATHS = QCC_OPTION_COMPILER + ".includePath";

    private static final String QCC_OPTION_LINKER = QCC_OPTION + ".linker";
    private static final String QCC_OPTION_LINKER_LIBRARIES = QCC_OPTION_LINKER + ".libraries";
    private static final String QCC_OPTION_LINKER_LIBRARY_PATHS = QCC_OPTION_LINKER + ".libraryPaths";

    private static final String QCC_TOOL = "com.qnx.qcc.tool";
    private static final String QCC_TOOL_COMPILER = QCC_TOOL + ".compiler";
    private static final String QCC_TOOL_ASSEMBLER = QCC_TOOL + ".assembler";
    private static final String QCC_TOOL_LINKER = QCC_TOOL + ".linker";
    private static final String QCC_TOOL_ARCHIVER = QCC_TOOL + ".archiver";

    public static final String QNX_USR_SEARCH;

    private static Cproject cproject;

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

    public static Configuration getBuildConfiguration( String target ) {

        return CProjectService.getBuildConfiguration( cproject, CProjectService.MODULE_SETTINGS, target );
    }

    public static ToolChain getToolChain( String target ) {

        Configuration configuration = getBuildConfiguration( target );
        return CProjectService.getToolChain( configuration );
    }

    public static String getCpu( ToolChain toolChain ) {

        String value = CProjectService.getToolChainOptionValue( toolChain, QCC_OPTION_CPU );
        return value.substring( QCC_OPTION_GEN_CPU.length() + 1 );
    }

    public static boolean isDebug( Tool tool ) {

        String superClass = tool.getSuperClass();
        String value = CProjectService.getToolOptionValue( tool, superClass + ".debug" );
        return Boolean.parseBoolean( value );
    }

    public static boolean useSecurity( Tool tool ) {

        String superClass = tool.getSuperClass();
        String value = CProjectService.getToolOptionValue( tool, superClass + ".security" );
        return Boolean.parseBoolean( value );
    }

    public static String[] getCompilerIncludePaths( ToolChain toolChain ) {

        Tool tool = CProjectService.getTool( toolChain, QCC_TOOL_COMPILER );
        return CProjectService.getToolOptionValues( tool, QCC_OPTION_COMPILER_INCLUDE_PATHS );
    }

    public static String[] getCompilerDefines( ToolChain toolChain ) {

        Tool tool = CProjectService.getTool( toolChain, QCC_TOOL_COMPILER );
        return CProjectService.getToolOptionValues( tool, QCC_OPTION_COMPILER_DEFINES );
    }

    public static String[] getLinkerLibraries( ToolChain toolChain ) {

        Tool tool = CProjectService.getTool( toolChain, QCC_TOOL_LINKER );
        return CProjectService.getToolOptionValues( tool, QCC_OPTION_LINKER_LIBRARIES );
    }

    public static String[] getLinkerLibraryPaths( ToolChain toolChain ) {

        Tool tool = CProjectService.getTool( toolChain, QCC_TOOL_LINKER );
        return CProjectService.getToolOptionValues( tool, QCC_OPTION_LINKER_LIBRARY_PATHS );
    }

    public static String getQnxTargetBaseDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PropertiesService.getProperty( PropertiesService.BLACKBERRY_NDK_HOME ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( "target" );
        return stringBuffer.toString();
    }

    public static String getQnxTargetPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getQnxTargetBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( getQnxTargetDirName() );
        return stringBuffer.toString();
    }

    public static String getQnxTargetDirName() {

        File file = new File( getQnxTargetBaseDirPath() );

        for (String filename : file.list()) {
            if ( filename.startsWith( "qnx" ) ) {
                return filename;
            }
        }

        return null;
    }

    public static String getQnxHostBaseDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PropertiesService.getProperty( PropertiesService.BLACKBERRY_NDK_HOME ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( "host" );
        return stringBuffer.toString();
    }

    public static File getQnxHostBaseDir() {

        return new File( getQnxHostBaseDirPath() );
    }

    public static String getQnxHostDirPath() {

        File file = new File( getQnxUsrPath() );
        return file.getParent();
    }

    public static String getQnxHostBinPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getQnxUsrPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "bin" );
        return stringBuffer.toString();
    }

    public static String getQnxUsrPath() {

        String[] includes = new String[] { QNX_USR_SEARCH };
        String[] files = FileUtils.listDirectoryPathsRecursive( getQnxHostBaseDir(), includes, null );

        if ( files.length > 0 ) {
            if ( files.length > 1 ) {
                JoJoMojo.getMojo().getLog().warn( "Multiple paths for `" + QNX_USR_SEARCH + "` have been located... using the first" );
                for (int i = 0; i < files.length; i++) {
                    JoJoMojo.getMojo().getLog().warn( "\t" + files[i] + ( i == 0 ? " <--- using" : "" ) );
                }
            }
            return files[0];
        }

        return null;
    }

    public static Cproject getCProject() {

        return cproject;
    }

    public static void loadCProject() {

        cproject = CProjectService.readCProject( getCProjectFile() );
    }
}
