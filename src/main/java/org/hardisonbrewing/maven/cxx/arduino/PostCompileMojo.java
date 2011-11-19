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
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;

/**
 * @goal arduino-post-compile
 * @phase compile
 */
public final class PostCompileMojo extends JoJoMojoImpl {

    private List<String> eepromCmd;
    private List<String> hexCmd;

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

        eepromCmd = new ArrayList<String>();
        eepromCmd.add( binPrefix + "avr-objcopy" );
        eepromCmd.add( "-O" );
        eepromCmd.add( "ihex" );
        eepromCmd.add( "-j" );
        eepromCmd.add( ".eeprom" );
        eepromCmd.add( "--set-section-flags=.eeprom=alloc,load" );
        eepromCmd.add( "--no-change-warnings" );
        eepromCmd.add( "--change-section-lma" );
        eepromCmd.add( ".eeprom=0" );

        hexCmd = new ArrayList<String>();
        hexCmd.add( binPrefix + "avr-objcopy" );
        hexCmd.add( "-O" );
        hexCmd.add( "ihex" );
        hexCmd.add( "-R" );
        hexCmd.add( ".eeprom" );
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
            postCompileSketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException fnfe) {
            getLog().error( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
            throw new IllegalStateException();
        }
    }

    private void postCompileSketch( Sketch sketch, Target target ) {

        /* 
           Build the two files (flash- and eeprom-code) 
           that get uploaded to the board 
        */
        List<String> eepromBuildCmd = new ArrayList<String>( eepromCmd );
        eepromBuildCmd.add( sketch.getBuildRootPath( target ) + File.separator + sketch.getName() + ".elf" );
        eepromBuildCmd.add( sketch.getBuildRootPath( target ) + File.separator + sketch.getName() + ".eep" );
        execute( eepromBuildCmd );

        List<String> hexBuildCmd = new ArrayList<String>( hexCmd );
        hexBuildCmd.add( sketch.getBuildRootPath( target ) + File.separator + sketch.getName() + ".elf" );
        hexBuildCmd.add( sketch.getBuildRootPath( target ) + File.separator + sketch.getName() + ".hex" );
        execute( hexBuildCmd );
    }
}
