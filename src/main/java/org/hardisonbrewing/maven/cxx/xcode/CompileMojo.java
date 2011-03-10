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

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TargetDirectoryService;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.PropertiesService;

/**
 * @goal xcode-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    /**
     * @parameter
     */
    public String configuration;

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
        cmd.add( XCodeService.getProject() + ".xcodeproj" );

        String targetCanonical = FileUtils.getProjectCanonicalPath( TargetDirectoryService.getTargetDirectoryPath() );

        cmd.add( "SYMROOT=$(PROJECT_DIR)" + targetCanonical );

        cmd.add( "BUILD_DIR=$(PROJECT_DIR)" + targetCanonical );

        String keychain = PropertiesService.getProperty( "xcode.keychain" );
        if ( keychain != null ) {
            cmd.add( "OTHER_CODE_SIGN_FLAGS=--keychain " + keychain );
        }

        //        cmd.add( "CODE_SIGN_IDENTITY=iPhone Developer: Macy Build (L5NLHD57L6)" );

        StringBuffer objroot = new StringBuffer();
        objroot.append( "OBJROOT=$(PROJECT_DIR)" );
        objroot.append( targetCanonical );
        objroot.append( File.separator );
        objroot.append( "OBJROOT" );
        cmd.add( objroot.toString() );

        StringBuffer configurationBuildDir = new StringBuffer();
        configurationBuildDir.append( "CONFIGURATION_BUILD_DIR=$(BUILD_DIR)" );
        configurationBuildDir.append( File.separator );
        if ( configuration == null ) {
            configurationBuildDir.append( XCodeService.DEFAULT_CONFIGURATION );
        }
        else {
            configurationBuildDir.append( configuration );
        }
        cmd.add( configurationBuildDir.toString() );

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

    protected Commandline buildCommandline( List<String> cmd ) {

        Commandline commandLine;

        try {
            commandLine = CommandLineService.build( cmd );
        }
        catch (CommandLineException e) {
            throw new IllegalStateException( e.getMessage() );
        }

        return commandLine;
    }
}
