/**
 * Copyright (c) 2010-2012 Martin M Reed
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
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;

/**
 * @goal xcode-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            execute( scheme );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                execute( target );
            }
        }
    }

    private void execute( String target ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcodebuild" );

        String workspacePath = XCodeService.getXcworkspacePath();
        if ( workspacePath != null ) {

            cmd.add( "-workspace" );
            cmd.add( workspacePath );

            cmd.add( "-scheme" );
            cmd.add( target );
        }
        else {

            cmd.add( "-project" );
            cmd.add( XCodeService.getXcprojPath() );

            cmd.add( "-target" );
            cmd.add( target );
        }

        String configuration = XCodeService.getConfiguration( target );
        cmd.add( "-configuration" );
        cmd.add( configuration );

        String targetBuildDirPath = TargetDirectoryService.getTargetBuildDirPath( target );

        cmd.add( "SYMROOT=" + targetBuildDirPath );

        cmd.add( "BUILD_DIR=" + targetBuildDirPath );

        String codeSignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codeSignIdentity != null ) {
            cmd.add( "CODE_SIGN_IDENTITY=" + codeSignIdentity );
        }

        StringBuffer objroot = new StringBuffer();
        objroot.append( "OBJROOT=" );
        objroot.append( targetBuildDirPath );
        objroot.append( File.separator );
        objroot.append( "OBJROOT" );
        cmd.add( objroot.toString() );

        StringBuffer configurationBuildDir = new StringBuffer();
        configurationBuildDir.append( "CONFIGURATION_BUILD_DIR=$(BUILD_DIR)" );
        configurationBuildDir.append( File.separator );
        configurationBuildDir.append( configuration );
        cmd.add( configurationBuildDir.toString() );

        execute( cmd );
    }

    @Override
    protected Commandline buildCommandline( List<String> cmd ) {

        try {
            return CommandLineService.build( cmd );
        }
        catch (CommandLineException e) {
            throw new IllegalStateException( e );
        }
    }
}