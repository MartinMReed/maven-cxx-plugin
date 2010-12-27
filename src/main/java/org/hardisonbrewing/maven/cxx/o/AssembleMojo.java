/**
 * Copyright (c) 2010 Martin M Reed
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

package org.hardisonbrewing.maven.cxx.o;

import java.util.LinkedList;
import java.util.List;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.Sources;

/**
 * @goal o-assemble
 * @phase compile
 * @requiresDependencyResolution o-compile
 */
public class AssembleMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String language;

    @Override
    public void execute() {

        String[] sources = TargetDirectoryService.getSourceFilePaths();

        if ( sources == null ) {
            return;
        }

        for (int i = 0; i < sources.length; i++) {

            List<String> cmd = new LinkedList<String>();
            cmd.add( "c++".equals( language ) ? "g++" : "gcc" );

            cmd.add( "-o" );
            cmd.add( Sources.replaceExtension( sources[i], "o" ) );

            cmd.add( "-c" );
            cmd.add( Sources.replaceExtension( sources[i], "s" ) );

            execute( cmd );
        }
    }
}
