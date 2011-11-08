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

import org.hardisonbrewing.maven.cxx.ProjectService;

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

    private static Cproject cproject;

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

    public static boolean isDebug( ToolChain toolChain ) {

        String superClass = toolChain.getSuperClass();
        String value = CProjectService.getToolChainOptionValue( toolChain, superClass + ".debug" );
        return Boolean.parseBoolean( value );
    }

    public static boolean useSecurity( ToolChain toolChain ) {

        String superClass = toolChain.getSuperClass();
        String value = CProjectService.getToolChainOptionValue( toolChain, superClass + ".security" );
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

    public static Cproject getCProject() {

        return cproject;
    }

    public static void loadCProject() {

        cproject = CProjectService.readCProject( getCProjectFile() );
    }
}
