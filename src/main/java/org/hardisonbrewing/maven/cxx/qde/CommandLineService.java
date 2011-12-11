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

import org.apache.commons.cli.CommandLine;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Utility methods for handling {@link CommandLine} execution.
 */
public class CommandLineService extends org.hardisonbrewing.maven.core.cli.CommandLineService {

    protected CommandLineService() {

        // do nothing
    }

    public static void addQnxEnvVars( Commandline commandLine ) {

        StringBuffer qnxHostBinDirPath = new StringBuffer();
        qnxHostBinDirPath.append( QdeService.getQnxHostUsrDirPath() );
        qnxHostBinDirPath.append( File.separator );
        qnxHostBinDirPath.append( "bin" );
        appendEnvVar( commandLine, "PATH", qnxHostBinDirPath.toString() );

        if ( !PropertiesService.hasProperty( PropertiesService.ENV_QNX_HOST ) ) {
            commandLine.addEnvironment( PropertiesService.QNX_HOST, QdeService.getQnxHostDirPath() );
        }

        if ( !PropertiesService.hasProperty( PropertiesService.ENV_QNX_TARGET ) ) {
            commandLine.addEnvironment( PropertiesService.QNX_TARGET, QdeService.getQnxTargetDirPath() );
        }
    }
}
