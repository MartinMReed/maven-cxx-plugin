/**
 * Copyright (c) 2010-2011 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.a;

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal a-archive
 * @phase compile
 */
public final class ArchiveMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String[] sources = TargetDirectoryService.getProcessableSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No source files. Skipping..." );
            return;
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "ar" );
        cmd.add( "-rs" );

        cmd.add( getProject().getArtifactId() + ".a" );

        for (int i = 0; i < sources.length; i++) {
            cmd.add( SourceFiles.replaceExtension( sources[i], "o" ) );
        }

        execute( cmd );
    }
}
