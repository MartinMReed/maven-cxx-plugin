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

package org.hardisonbrewing.maven.cxx.xcode;

import java.util.LinkedList;
import java.util.List;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-compile
 * @phase compile
 * @requiresDependencyResolution compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter expression="${configuration.project}"
     */
    public String project;

    /**
     * @parameter
     */
    public String target;

    /**
     * @parameter
     */
    public boolean activeTarget;

    /**
     * @parameter
     */
    public boolean allTargets;

    @Override
    public void execute() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcodebuild" );

        cmd.add( "-project" );
        cmd.add( project + ".xcodeproj" );

        if ( target != null ) {
            cmd.add( "-target" );
            cmd.add( target );
        }
        else if ( activeTarget ) {
            cmd.add( "-activetarget" );
        }
        else if ( allTargets ) {
            cmd.add( "-alltargets" );
        }

        execute( cmd );
    }
}
