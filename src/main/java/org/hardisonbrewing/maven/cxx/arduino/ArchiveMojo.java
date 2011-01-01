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
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;
import org.wayoda.ang.utils.FileSelector;
import org.wayoda.ang.utils.FileUtils;

/**
 * @goal arduino-archive
 * @phase archive
 * @requiresDependencyResolution archive
 */
public final class ArchiveMojo extends JoJoMojoImpl {

    private List<String> archCmd;

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

        archCmd = new ArrayList<String>();
        archCmd.add( binPrefix + "avr-ar" );
        archCmd.add( "rcs" );
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
            archiveSketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException fnfe) {
            getLog().error( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
            throw new IllegalStateException();
        }
    }

    private void archiveSketch( Sketch sketch, Target target ) {

        //check that the build directory for the core exists
        File outputDir = sketch.getCoreBuildRoot( target );
        if ( outputDir == null ) {
            getLog().error( "Compiling Core failed. Output directory `" + sketch.getBuildRootPath( target ) + File.separator + "core` does not exist or cannot be read or written" );
            throw new IllegalStateException();
        }

        for (File f : FileUtils.getFiles( outputDir, new FileSelector.ObjectFileFilter() )) {
            ArrayList<String> arcmd = new ArrayList<String>( archCmd );
            arcmd.add( outputDir.getPath() + File.separator + Core.CORE_ARCHIVE_NAME );
            arcmd.add( f.getPath() );
            execute( arcmd );
        }
    }
}
