/**
 * Copyright (c) 2010-2013 Martin M Reed
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-convert-plist
 * @phase compile
 */
public final class ConvertPlistMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            String target = XCodeService.getBuildTargetName( scheme );
            execute( target );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                execute( target );
            }
        }
    }

    private void execute( String target ) {

        String plistPath = XCodeService.getEmbeddedInfoPlistPath( target );
        File plistFile = new File( plistPath );

        if ( !plistFile.exists() ) {
            getLog().error( "Unable to locate embedded " + InfoPlistService.INFO_PLIST + ": " + plistFile );
            throw new IllegalStateException();
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "plutil" );
        cmd.add( "-convert" );
        cmd.add( "xml1" );
        cmd.add( "-o" );
        cmd.add( XCodeService.getConvertedInfoPlistPath( target ) );
        cmd.add( plistPath );
        execute( cmd );
    }
}
