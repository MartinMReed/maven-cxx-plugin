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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;

/**
 * @goal arduino-deploy
 * @phase deploy
 */
public final class DeployMojo extends JoJoMojoImpl {

    protected ArrayList<String> baseCmd;

    /**
     * @parameter
     */
    public String sketchbook;

    /**
     * @parameter
     */
    public String targetDevice;

    /**
     * @parameter
     */
    private String baudrate;

    private void prepareCommands() {

        String binPrefix = ProjectService.getProperty( "avr.bin" );
        if ( binPrefix != null && !binPrefix.endsWith( File.separator ) ) {
            binPrefix += File.separator;
        }

        baseCmd = new ArrayList<String>();
        baseCmd.add( binPrefix + "avrdude" );

        String confPath = ProjectService.getProperty( "avrdude.config.path" );
        if ( confPath != null ) {
            baseCmd.add( "-C" );
            baseCmd.add( confPath );
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( targetDevice == null ) {
            throw new IllegalArgumentException( "<targetDevice /> must be set." );
        }

        if ( sketchbook == null ) {
            throw new IllegalArgumentException( "<sketchbook /> must be set." );
        }

        if ( ProjectService.getProperty( "serial.port" ) == null ) {
            throw new IllegalArgumentException( "Property `serial.port` must be set." );
        }

        prepareCommands();

        ArduinoBuildEnvironment arduinoBuildEnvironment = ArduinoBuildEnvironment.getInstance();
        Target target = arduinoBuildEnvironment.getDefaultTargetList().getTarget( targetDevice );
        if ( target == null ) {
            getLog().error( "Unknown target '" + targetDevice + "'" );
            throw new IllegalArgumentException();
        }

        try {
            uploadSketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException fnfe) {
            getLog().error( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
            throw new IllegalStateException();
        }
    }

    private final void uploadSketch( Sketch sketch, Target target ) {

        File hexFile = sketch.getFlash( target );
        if ( hexFile == null ) {
            getLog().error( "No upload data found" );
            throw new IllegalStateException();
        }

        ArrayList<String> cmd = new ArrayList<String>();
        cmd.addAll( baseCmd );
        cmd.add( "-c" );
        cmd.add( "stk500v1" );
        cmd.add( "-p" );
        cmd.add( target.getMCU() );
        cmd.add( "-P" );
        cmd.add( ProjectService.getProperty( "serial.port" ) );
        cmd.add( "-b" );
        cmd.add( baudrate != null ? baudrate : target.getUploadSpeed() );
        cmd.add( "-D" );
        cmd.add( "-Uflash:w:" + hexFile.getPath() + ":i" );
        execute( cmd );
    }
}
