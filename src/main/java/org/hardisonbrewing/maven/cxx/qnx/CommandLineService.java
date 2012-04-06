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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.cxx.qnx.QnxService;

/**
 * Utility methods for handling {@link CommandLine} execution.
 */
public class CommandLineService extends org.hardisonbrewing.maven.core.cli.CommandLineService {

    protected CommandLineService() {

        // do nothing
    }

    public static void addQnxEnvVarArgs( List<String> cmd ) {

        cmd.add( "-D" );
        cmd.add( getQnxHostEnvVarPair() );

        cmd.add( "-D" );
        cmd.add( getQnxTargetEnvVarPair() );
    }

    private static String getQnxHostEnvVarPair() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PropertiesService.QNX_HOST );
        stringBuffer.append( "=" );
        if ( PropertiesService.hasProperty( PropertiesService.ENV_QNX_HOST ) ) {
            stringBuffer.append( PropertiesService.getProperty( PropertiesService.ENV_QNX_HOST ) );
        }
        else {
            stringBuffer.append( QnxService.getQnxHostDirPath() );
        }
        return stringBuffer.toString();
    }

    private static String getQnxTargetEnvVarPair() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PropertiesService.QNX_TARGET );
        stringBuffer.append( "=" );
        if ( PropertiesService.hasProperty( PropertiesService.ENV_QNX_TARGET ) ) {
            stringBuffer.append( PropertiesService.getProperty( PropertiesService.ENV_QNX_TARGET ) );
        }
        else {
            stringBuffer.append( QnxService.getQnxTargetDirPath() );
        }
        return stringBuffer.toString();
    }

    public static void addQnxEnvVars( Commandline commandLine ) {

        StringBuffer qnxHostBinDirPath = new StringBuffer();
        qnxHostBinDirPath.append( QnxService.getQnxHostUsrDirPath() );
        qnxHostBinDirPath.append( File.separator );
        qnxHostBinDirPath.append( "bin" );
        appendEnvVar( commandLine, "PATH", qnxHostBinDirPath.toString() );

        if ( !PropertiesService.hasProperty( PropertiesService.ENV_QNX_HOST ) ) {
            commandLine.addEnvironment( PropertiesService.QNX_HOST, QnxService.getQnxHostDirPath() );
        }

        if ( !PropertiesService.hasProperty( PropertiesService.ENV_QNX_TARGET ) ) {
            commandLine.addEnvironment( PropertiesService.QNX_TARGET, QnxService.getQnxTargetDirPath() );
        }
    }
}
