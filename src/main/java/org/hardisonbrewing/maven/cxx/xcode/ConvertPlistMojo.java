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

package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-convert-plist
 * @phase xcode-convert-plist
 */
public final class ConvertPlistMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() {

        if ( target != null ) {
            execute( target );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                execute( target );
            }
        }
    }

    private void execute( String target ) {

        String embeddedInfoPlistPath = XCodeService.getEmbeddedInfoPlistPath( target );

        File embeddedInfoPlist = new File( embeddedInfoPlistPath );
        if ( !embeddedInfoPlist.exists() ) {
            return;
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "plutil" );
        cmd.add( "-convert" );
        cmd.add( "xml1" );
        cmd.add( "-o" );
        cmd.add( XCodeService.getConvertedInfoPlistPath( target ) );
        cmd.add( embeddedInfoPlistPath );
        execute( cmd );
    }
}
