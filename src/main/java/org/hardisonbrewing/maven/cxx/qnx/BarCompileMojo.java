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

import generated.net.rim.bar.AssetConfiguration;
import generated.net.rim.bar.BarDescriptor;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;

/**
 * @goal qnx-bar-compile
 * @phase compile
 */
public class BarCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();

        if ( barDescriptor == null ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "No " );
            stringBuffer.append( BarDescriptorService.BAR_DESCRIPTOR_FILENAME );
            stringBuffer.append( "... skipping" );
            getLog().info( stringBuffer.toString() );
            return;
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "blackberry-nativepackager" );

        cmd.add( "-package" );
        cmd.add( TargetDirectoryService.getBarPath( barDescriptor ) );

        cmd.add( BarDescriptorService.BAR_DESCRIPTOR_FILENAME );

        CommandLineService.addQnxEnvVarArgs( cmd );

        AssetConfiguration configuration = BarDescriptorService.getAssetConfiguration( barDescriptor, target );
        cmd.add( "-configuration" );
        cmd.add( configuration.getId() );

        Commandline commandLine = buildCommandline( cmd );
        commandLine.setWorkingDirectory( ProjectService.getBaseDirPath() );
        CommandLineService.addQnxEnvVars( commandLine );
        execute( commandLine );
    }
}
