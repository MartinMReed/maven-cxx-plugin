/**
 * Copyright (c) 2010-2013 Martin M Reed
 * Copyright (c) 2013 Todd Grooms
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
package org.hardisonbrewing.maven.cxx.xcode;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;

/**
 * @goal xcode-compile
 * @phase compile
 */
public final class CompileMojo extends AbstractCompileMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            getLog().info( "Scheme specified, skipping." );
            return;
        }

        OutputStream outputStream = null;

        try {

            outputStream = new FileOutputStream( TargetDirectoryService.getBuildLogFile() );
            StreamConsumer systemOut = new LogCopyStreamConsumer( outputStream, LogStreamConsumer.LEVEL_INFO );
            StreamConsumer systemErr = new LogCopyStreamConsumer( outputStream, LogStreamConsumer.LEVEL_ERROR );

            for (String target : XCodeService.getTargets()) {
                List<String> cmd = buildCommand( target );
                Properties buildSettings = loadBuildSettings( cmd );
                PropertiesService.storeBuildSettings( buildSettings, target );
                execute( cmd, systemOut, systemErr );
            }
        }
        catch (Exception e) {
            throw new IllegalStateException( e );
        }
        finally {
            IOUtil.close( outputStream );
        }
    }
}
