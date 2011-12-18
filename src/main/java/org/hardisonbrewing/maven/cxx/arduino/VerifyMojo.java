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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;
import org.wayoda.ang.tools.HexFileSizer;

/**
 * @goal arduino-verify
 * @phase verify
 */
public final class VerifyMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String sketchbook;

    /**
     * @parameter
     */
    public String targetDevice;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( targetDevice == null ) {
            getLog().error( "<targetDevice /> must be set." );
            throw new IllegalArgumentException();
        }

        if ( sketchbook == null ) {
            getLog().error( "<sketchbook /> must be set." );
            throw new IllegalArgumentException();
        }

        ArduinoBuildEnvironment arduinoBuildEnvironment = ArduinoBuildEnvironment.getInstance();
        Target target = arduinoBuildEnvironment.getDefaultTargetList().getTarget( targetDevice );
        if ( target == null ) {
            getLog().error( "Unknown target '" + targetDevice + "'" );
            throw new IllegalArgumentException();
        }

        try {
            verifySketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException fnfe) {
            getLog().error( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
            throw new IllegalStateException();
        }
    }

    /**
     * Checks the flash memory size used by the code
     * @return boolean Returns true if the resulting code fits into the
     * flash memory of the target hardware. False otherwise.
     */
    private void verifySketch( Sketch sketch, Target target ) {

        File f = sketch.getFlash( target );
        try {
            int size = HexFileSizer.getSize( f );
            int maxSize = target.getUploadSize();
            if ( size >= maxSize ) {
                getLog().error( "Sketch `" + sketch.getName() + "` for target `" + target.getName() + "` is too big. Bytes used: " + size + " Maximum bytes " + maxSize );
                throw new IllegalStateException();
            }
            else {
                getLog().info( "Sketch `" + sketch.getName() + "` for target `" + target.getName() + "` uses " + size + " bytes of a " + maxSize + " bytes maximum" );
            }
        }
        catch (Exception e) {
            getLog().error( "Can't find hex-file for Sketch `" + sketch.getName() + "` Target `" + target.getName() + "`" );
            throw new IllegalStateException();
        }
    }
}
