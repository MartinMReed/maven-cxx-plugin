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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QccService {

    private static QccCommand[] compileQccCommands;

    public static boolean isQccCommand( String command ) {

        Pattern pattern = Pattern.compile( "^[^\\s]*qcc\\s" );
        Matcher matcher = pattern.matcher( command );
        return matcher.find();
    }

    private static void parseStaticLib( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-[aA]\\s*([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        if ( matcher.find() ) {
            qccCommand.staticLib = true;
            qccCommand.outputFile = matcher.group( 1 );
        }
    }

    private static void parseApplication( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-o\\s*([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        if ( matcher.find() ) {
            qccCommand.application = true;
            qccCommand.outputFile = matcher.group( 1 );
        }
    }

    private static void parseCompiler( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-V\\s*([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        if ( matcher.find() ) {
            qccCommand.compiler = matcher.group( 1 );
        }
    }

    private static void parseVariants( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-DVARIANT_([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        List<String> variants = new ArrayList<String>();

        while (matcher.find()) {
            variants.add( matcher.group( 1 ) );
        }

        String[] _variants = new String[variants.size()];
        variants.toArray( _variants );
        qccCommand.variants = _variants;
    }

    private static void parseBuildEnvs( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-DBUILDENV_([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        List<String> buildEnvs = new ArrayList<String>();

        while (matcher.find()) {
            buildEnvs.add( matcher.group( 1 ) );
        }

        String[] _buildEnvs = new String[buildEnvs.size()];
        buildEnvs.toArray( _buildEnvs );
        qccCommand.buildEnvs = _buildEnvs;
    }

    private static void parseDirIncludes( QccCommand qccCommand ) {

        Pattern pattern = Pattern.compile( "\\-I\\s*([^\\s]+)" );
        Matcher matcher = pattern.matcher( qccCommand.command );

        List<String> dirIncludes = new ArrayList<String>();

        while (matcher.find()) {
            dirIncludes.add( matcher.group( 1 ) );
        }

        String[] _dirIncludes = new String[dirIncludes.size()];
        dirIncludes.toArray( _dirIncludes );
        qccCommand.dirIncludes = _dirIncludes;
    }

    public static QccCommand parseQccCommand( String command ) {

        QccCommand qccCommand = new QccCommand();
        qccCommand.command = command;
        parseStaticLib( qccCommand );
        parseDirIncludes( qccCommand );
        parseApplication( qccCommand );
        parseCompiler( qccCommand );
        parseVariants( qccCommand );
        parseBuildEnvs( qccCommand );
        return qccCommand;
    }

    public static final class QccCommand {

        String command;
        String[] dirIncludes;
        String compiler;
        String[] variants;
        String[] buildEnvs;
        boolean staticLib;
        boolean application;
        String outputFile;

        public void printDebug() {

            System.out.println( "QCC Command:" );

            for (String dirInclude : dirIncludes) {
                System.out.println( "\tDIRINC: " + dirInclude );
            }

            for (String variant : variants) {
                System.out.println( "\tVARIANT_: " + variant );
            }

            for (String buildEnv : buildEnvs) {
                System.out.println( "\tBUILDENV_: " + buildEnv );
            }

            System.out.println( "\tCOMPILER: " + compiler );
            System.out.println( "\tSTATICLIB: " + ( staticLib ? outputFile : "false" ) );
            System.out.println( "\tAPPLICATION: " + ( application ? outputFile : "false" ) );
        }
    }

    public static QccCommand[] getCompileQccCommands() {

        return compileQccCommands;
    }

    public static void setCompileQccCommands( QccCommand[] compileQccCommands ) {

        QccService.compileQccCommands = compileQccCommands;
    }
}
