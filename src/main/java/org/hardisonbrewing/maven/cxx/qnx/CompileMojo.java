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
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;
import org.hardisonbrewing.maven.cxx.qnx.QccService.QccCommand;

/**
 * @goal qnx-compile
 * @phase compile
 */
public class CompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "make" );
        cmd.add( "all" );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addQnxEnvVars( commandLine );
        commandLine.setWorkingDirectory( ProjectService.getBaseDirPath() );

        List<QccCommand> qccCommands = new ArrayList<QccCommand>();

        StreamConsumer systemOut = new MyLogStreamConsumer( qccCommands, LogStreamConsumer.LEVEL_INFO );
        StreamConsumer systemErr = new MyLogStreamConsumer( qccCommands, LogStreamConsumer.LEVEL_ERROR );
        execute( commandLine, systemOut, systemErr );

        QccCommand[] _qccCommands = new QccCommand[qccCommands.size()];
        qccCommands.toArray( _qccCommands );
        QccService.setCompileQccCommands( _qccCommands );
    }

    private final class MyLogStreamConsumer extends LogStreamConsumer {

        private final List<QccCommand> qccCommands;

        public MyLogStreamConsumer(List<QccCommand> qccCommands, int level) {

            super( level );

            this.qccCommands = qccCommands;
        }

        @Override
        public void consumeLine( String line ) {

            super.consumeLine( line );

            if ( QccService.isQccCommand( line ) ) {
                QccCommand qccCommand = QccService.parseQccCommand( line );
                qccCommands.add( qccCommand );
            }
        }
    }
}
