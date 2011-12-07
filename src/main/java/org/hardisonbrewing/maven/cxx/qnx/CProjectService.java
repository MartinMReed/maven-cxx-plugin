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

import java.util.ArrayList;
import java.util.List;

public class CProjectService extends org.hardisonbrewing.maven.cxx.cdt.CProjectService {

    private static final String QCC_OPTION = "com.qnx.qcc.option";
    private static final String QCC_OPTION_CPU = QCC_OPTION + ".cpu";
    private static final String QCC_OPTION_GEN_CPU = QCC_OPTION + ".gen.cpu";

    private static final String QCC_OPTION_COMPILER = QCC_OPTION + ".compiler";
    private static final String QCC_OPTION_COMPILER_DEFINES = QCC_OPTION_COMPILER + ".defines";
    private static final String QCC_OPTION_COMPILER_INCLUDE_PATHS = QCC_OPTION_COMPILER + ".includePath";

    private static final String QCC_OPTION_ASSEMBLER = QCC_OPTION + ".assembler";

    private static final String QCC_OPTION_LINKER = QCC_OPTION + ".linker";
    private static final String QCC_OPTION_LINKER_LIBRARIES = QCC_OPTION_LINKER + ".libraries";
    private static final String QCC_OPTION_LINKER_LIBRARY_PATHS = QCC_OPTION_LINKER + ".libraryPaths";

    private static final String QCC_OPTION_ARCHIVER = QCC_OPTION + ".archiver";

    private static final String QCC_TOOL = "com.qnx.qcc.tool";
    public static final String QCC_TOOL_COMPILER = QCC_TOOL + ".compiler";
    public static final String QCC_TOOL_ASSEMBLER = QCC_TOOL + ".assembler";
    public static final String QCC_TOOL_LINKER = QCC_TOOL + ".linker";
    public static final String QCC_TOOL_ARCHIVER = QCC_TOOL + ".archiver";

    private static Cproject cproject;

    public static Configuration[] getBuildConfigurations() {

        return getBuildConfigurations( getCProject(), MODULE_SETTINGS );
    }

    public static Configuration getBuildConfiguration( String target ) {

        return getBuildConfiguration( getCProject(), MODULE_SETTINGS, target );
    }

    public static ToolChain getToolChain( String target ) {

        Configuration configuration = getBuildConfiguration( target );
        return getToolChain( configuration );
    }

    public static String getCompilerPlatform( ToolChain toolChain ) {

        String compiler = QnxService.getDefaultCompiler();
        String version = QnxService.getDefaultCompilerVersion( compiler );
        String platform = getPlatform( toolChain );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( version );
        stringBuffer.append( "," );
        stringBuffer.append( compiler );
        stringBuffer.append( "_nto" );
        stringBuffer.append( getCpu( platform ) );

        String[] cpuVariant = getCpuVariant( platform );
        if ( cpuVariant != null ) {
            for (String variant : cpuVariant) {
                stringBuffer.append( variant );
            }
        }

        String endian = getEndian( platform );
        if ( endian != null ) {
            stringBuffer.append( endian );
        }

        return stringBuffer.toString();
    }

    public static String getPlatform( ToolChain toolChain ) {

        String value = getToolChainOptionValue( toolChain, QCC_OPTION_CPU );
        return value.substring( QCC_OPTION_GEN_CPU.length() + 1 );
    }

    public static String getCpu( String platform ) {

        int hyphen = platform.indexOf( '-' );
        if ( hyphen >= 0 ) {
            platform = platform.substring( 0, hyphen );
        }

        String endian = getEndian( platform );
        if ( endian != null ) {
            platform = platform.substring( 0, platform.length() - endian.length() );
        }

        return platform;
    }

    public static String getEndian( String platform ) {

        int hyphen = platform.indexOf( '-' );
        if ( hyphen >= 0 ) {
            platform = platform.substring( 0, hyphen );
        }
        if ( platform.endsWith( "be" ) ) {
            return "be";
        }
        if ( platform.endsWith( "le" ) ) {
            return "le";
        }
        return null;
    }

    public static String[] getCpuVariant( String platform ) {

        List<String> result = new ArrayList<String>( 2 );

        int start = 0;
        while (( start = platform.indexOf( '-', start ) ) != -1) {

            start++;
            int end = platform.indexOf( '-', start );

            if ( end == -1 ) {
                result.add( platform.substring( start ) );
                break;
            }

            result.add( platform.substring( start, end ) );

            start = end + 1;
        }

        if ( result.isEmpty() ) {
            return null;
        }

        String[] _result = new String[result.size()];
        result.toArray( _result );
        return _result;
    }

    public static int getOptLevel( Tool tool ) {

        String superClass = getQccOptionClass( tool );
        String optLevel = superClass + ".optlevel";
        String value = getToolOptionValue( tool, optLevel );

        if ( value == null ) {
            return -1;
        }

        value = value.substring( ( optLevel + "." ).length() );

        try {
            return Integer.parseInt( value );
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String getQccOptionClass( Tool tool ) {

        String superClass = tool.getSuperClass();
        if ( QCC_TOOL_COMPILER.equals( superClass ) ) {
            return QCC_OPTION_COMPILER;
        }
        else if ( QCC_TOOL_ASSEMBLER.equals( superClass ) ) {
            return QCC_OPTION_ASSEMBLER;
        }
        else if ( QCC_TOOL_LINKER.equals( superClass ) ) {
            return QCC_OPTION_LINKER;
        }
        else if ( QCC_TOOL_ARCHIVER.equals( superClass ) ) {
            return QCC_OPTION_ARCHIVER;
        }
        throw new IllegalArgumentException( "Unknown tool: " + superClass );
    }

    public static boolean isDebug( Tool tool ) {

        String superClass = tool.getSuperClass();

        if ( superClass.equals( QCC_TOOL_COMPILER ) ) {
            superClass = QCC_OPTION + ".compile";
        }
        else {
            superClass = getQccOptionClass( tool );
        }

        String value = getToolOptionValue( tool, superClass + ".debug" );
        return Boolean.parseBoolean( value );
    }

    public static boolean useCodeCoverage( Tool tool ) {

        String superClass = getQccOptionClass( tool );
        String value = getToolOptionValue( tool, superClass + ".coverage" );
        return Boolean.parseBoolean( value );
    }

    public static boolean useProfile( Tool tool ) {

        String superClass = getQccOptionClass( tool );
        String value = getToolOptionValue( tool, superClass + ".profile2" );
        return Boolean.parseBoolean( value );
    }

    public static boolean usePie( Tool tool ) {

        String superClass = getQccOptionClass( tool );
        String value = getToolOptionValue( tool, superClass + ".pie" );
        return Boolean.parseBoolean( value );
    }

    public static boolean useSecurity( Tool tool ) {

        String superClass = getQccOptionClass( tool );
        String value = getToolOptionValue( tool, superClass + ".security" );
        return Boolean.parseBoolean( value );
    }

    public static String[] getCompilerIncludePaths( ToolChain toolChain ) {

        Tool tool = getTool( toolChain, QCC_TOOL_COMPILER );
        return getToolOptionValues( tool, QCC_OPTION_COMPILER_INCLUDE_PATHS );
    }

    public static String[] getCompilerDefines( ToolChain toolChain ) {

        Tool tool = getTool( toolChain, QCC_TOOL_COMPILER );
        return getToolOptionValues( tool, QCC_OPTION_COMPILER_DEFINES );
    }

    public static String[] getLinkerLibraries( ToolChain toolChain ) {

        Tool tool = getTool( toolChain, QCC_TOOL_LINKER );
        return getToolOptionValues( tool, QCC_OPTION_LINKER_LIBRARIES );
    }

    public static String[] getLinkerLibraryPaths( ToolChain toolChain ) {

        Tool tool = getTool( toolChain, QCC_TOOL_LINKER );
        return getToolOptionValues( tool, QCC_OPTION_LINKER_LIBRARY_PATHS );
    }

    public static Cproject getCProject() {

        return cproject;
    }

    public static void loadCProject() {

        cproject = CProjectService.readCProject( QnxService.getCProjectFile() );
    }
}
