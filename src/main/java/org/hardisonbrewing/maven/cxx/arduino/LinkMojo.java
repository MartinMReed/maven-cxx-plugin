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

package org.hardisonbrewing.maven.cxx.arduino;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.wayoda.ang.libraries.Core;
import org.wayoda.ang.libraries.Library;
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;

/**
 * @goal arduino-link
 * @phase link
 * @requiresDependencyResolution link
 */
public final class LinkMojo extends JoJoMojoImpl {

    private List<String> lCmd;

    /**
     * @parameter
     */
    public String sketchbook;

    /**
     * @parameter
     */
    public String targetDevice;

    private void prepareCommands() {

        String binPrefix = ProjectService.getProperty( "avr.bin" );
        if ( binPrefix != null && !binPrefix.endsWith( File.separator ) ) {
            binPrefix += File.separator;
        }

        lCmd = new ArrayList<String>();
        lCmd.add( binPrefix + "avr-gcc" );
        lCmd.add( "-Os" );
        lCmd.add( "-Wl,--gc-sections" );
    }

    @Override
    public void execute() {

        if ( targetDevice == null ) {
            getLog().error( "<targetDevice /> must be set." );
            throw new IllegalArgumentException();
        }

        if ( sketchbook == null ) {
            getLog().error( "<sketchbook /> must be set." );
            throw new IllegalArgumentException();
        }

        prepareCommands();

        ArduinoBuildEnvironment arduinoBuildEnvironment = ArduinoBuildEnvironment.getInstance();
        Target target = arduinoBuildEnvironment.getDefaultTargetList().getTarget( targetDevice );
        if ( target == null ) {
            getLog().error( "Unknown target '" + targetDevice + "'" );
            throw new IllegalArgumentException();
        }

        try {
            linkSketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException fnfe) {
            getLog().error( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
            throw new IllegalStateException();
        }
    }

    private void linkSketch( Sketch sketch, Target target ) {

        /* Now link the core, library and user code */
        List<String> linkCmd = new ArrayList<String>( lCmd );
        linkCmd.add( "-mmcu=" + target.getMCU() );
        linkCmd.add( "-o" );
        linkCmd.add( sketch.getBuildRootPath( target ) + File.separator + sketch.getName() + ".elf" );

        /* link with every library that was referenced in the sketch */
        for (Library l : sketch.getLibraries()) {
            for (File f : sketch.getLibraryObjectFiles( target, l )) {
                linkCmd.add( f.getPath() );
            }
        }

        /* link all the objects that were compiled for the sketch */
        for (File f : sketch.getObjectFiles( target )) {
            linkCmd.add( f.getPath() );
        }

        /* link with the core-archive */
        linkCmd.add( sketch.getCoreBuildRoot( target ).getPath() + File.separator + Core.CORE_ARCHIVE_NAME );
        linkCmd.add( "-L" + sketch.getBuildRootPath( target ) );
        linkCmd.add( "-lm" );
        execute( linkCmd );
    }
}
