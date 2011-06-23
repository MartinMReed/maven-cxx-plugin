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
import org.wayoda.ang.libraries.CodeManager;
import org.wayoda.ang.libraries.Core;
import org.wayoda.ang.libraries.Library;
import org.wayoda.ang.project.ArduinoBuildEnvironment;
import org.wayoda.ang.project.Sketch;
import org.wayoda.ang.project.Target;

/**
 * @goal arduino-compile
 * @phase compile
 */
public final class CompileMojo extends JoJoMojoImpl {

    private List<String> cCmd;
    private List<String> cppCmd;

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

        cCmd = new ArrayList<String>();
        cCmd.add( binPrefix + "avr-gcc" );
        cCmd.add( "-c" );
        cCmd.add( "-g" );
        cCmd.add( "-Os" );
        cCmd.add( "-w" );
        cCmd.add( "-ffunction-sections" );
        cCmd.add( "-fdata-sections" );

        cppCmd = new ArrayList<String>();
        cppCmd.add( binPrefix + "avr-g++" );
        cppCmd.add( "-c" );
        cppCmd.add( "-g" );
        cppCmd.add( "-Os" );
        cppCmd.add( "-w" );
        cppCmd.add( "-fno-exceptions" );
        cppCmd.add( "-ffunction-sections" );
        cppCmd.add( "-fdata-sections" );
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
            throw new IllegalArgumentException( "Unknown target '" + targetDevice + "'" );
        }

        try {
            compileSketch( new Sketch( new File( sketchbook ) ), target );
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException( "<sketchbook=\"" + sketchbook + "\"/> is not a valid Sketch-Directory or does not contain valid Sketch files" );
        }
    }

    private void compileSketch( Sketch sketch, Target target ) {

        sketch.preprocess( target );

        Core core = CodeManager.getInstance().getCore( target );
        compileCore( sketch, target, core );

        for (Library library : sketch.getLibraries()) {
            compileLibrary( sketch, target, library, core );
        }

        compileSketch( sketch, target, core );
    }

    /**
     * Compiles a sketch for a specific target.
     * @param sketch the sketch to be compiled.
     * @param target the hardware target for which the files are to be compiled 
     * @return boolean True if no errors where detected during the compilation
     */
    private void compileSketch( Sketch sketch, Target target, Core core ) {

        /* Compile the C/C++ code generated from the users Arduino-code */
        ArrayList<String> includePathList = new ArrayList<String>();

        /* Add the core-source dir to the list of directories
           to be searched for header-files */
        includePathList.add( "-I" + core.getDirectory().getPath() );

        /* Add the source directory of every library referenced from our code */
        for (Library library : sketch.getLibraries()) {
            includePathList.add( "-I" + library.getDirectory().getPath() );
            if ( library.getUtilDirectory() != null ) {
                includePathList.add( "-I" + library.getUtilDirectory().getPath() );
            }
        }

        /* Add the target-dir of the Sketch too */
        includePathList.add( "-I" + sketch.getBuildRootPath( target ) );

        /* The compiler cmdLine for C code */
        ArrayList<String> baseC = new ArrayList<String>( cCmd );
        baseC.add( "-mmcu=" + target.getMCU() );
        baseC.add( "-DF_CPU=" + target.getSpeed() );
        baseC.addAll( includePathList );

        /* The compiler cmdLine for C++ code */
        ArrayList<String> baseCPP = new ArrayList<String>( cppCmd );
        baseCPP.add( "-mmcu=" + target.getMCU() );
        baseCPP.add( "-DF_CPU=" + target.getSpeed() );
        baseCPP.addAll( includePathList );

        for (File file : sketch.getSourceFiles( target )) {

            ArrayList<String> cmd;

            if ( file.getName().toLowerCase().endsWith( ".c" ) ) {
                cmd = new ArrayList<String>( baseC );
            }
            else if ( file.getName().toLowerCase().endsWith( ".cpp" ) ) {
                cmd = new ArrayList<String>( baseCPP );
            }
            else {
                //here we have some other type of file we don't have to compile
                continue;
            }

            cmd.add( file.getPath() );
            cmd.add( "-o" );
            cmd.add( sketch.getBuildRootPath( target ) + File.separator + file.getName() + ".o" );
            execute( cmd );
        }
    }

    /**
     * Compiles a libary used by a Sketch for a specific target. 
     * @param sketch the sketch for which the library is to be compiled.
     * @param target the hardware target for which the library is to be compiled 
     * @param library the library to compile 
     * @param core the core files for the platform
     * @return boolean True if no errors where detected during the compilation
     */
    private void compileLibrary( Sketch sketch, Target target, Library library, Core core ) {

        File coreSrcDir = core.getDirectory();
        if ( !coreSrcDir.exists() ) {
            getLog().error( "Compile library `" + library.getName() + "` failed. Target `" + target.getKey() + "` depends on unknown Core `" + target.getCore() + "`" );
            throw new IllegalStateException();
        }

        File outputDir = sketch.getLibraryBuildRoot( target, library );
        if ( outputDir == null ) {
            getLog().error( "Compile library `" + library.getName() + "` failed. Can find build directory for target `" + target.getKey() + "`" );
            throw new IllegalStateException();
        }

        /* build the list of directories  to be searched 
           for headers referenced from our code */
        List<String> includePathList = new ArrayList<String>();
        includePathList.add( "-I" + library.getDirectory().getPath() );
        if ( library.getUtilDirectory() != null ) {
            includePathList.add( "-I" + library.getUtilDirectory().getPath() );
        }
        includePathList.add( "-I" + coreSrcDir.getPath() );

        /* The compiler cmdLine for C code */
        List<String> baseC = new ArrayList<String>( cCmd );
        baseC.add( "-mmcu=" + target.getMCU() );
        baseC.add( "-DF_CPU=" + target.getSpeed() );
        baseC.addAll( includePathList );

        /* The compiler cmdLine for C++ code */
        List<String> baseCPP = new ArrayList<String>( cppCmd );
        baseCPP.add( "-mmcu=" + target.getMCU() );
        baseCPP.add( "-DF_CPU=" + target.getSpeed() );
        baseCPP.addAll( includePathList );

        for (File f : library.getSourceFiles()) {
            List<String> cmd;
            if ( f.getName().toLowerCase().endsWith( ".c" ) ) {
                cmd = new ArrayList<String>( baseC );
            }
            else if ( f.getName().toLowerCase().endsWith( ".cpp" ) ) {
                cmd = new ArrayList<String>( baseCPP );
            }
            else {
                continue;
            }
            cmd.add( f.getPath() );
            cmd.add( "-o" + outputDir.getPath() + File.separator + f.getName() + ".o" );
            execute( cmd );
        }

        getLog().info( "Compiled Library `" + library.getName() + "` target `" + target.getKey() + " = " + target.getName() + "`" );
    }

    /**
     * Compiles the core source-files for a sketch and a specific target. 
     * @param sketch the sketch for which the core is to be compiled.
     * @param target the hardware target for which the core is to be compiled 
     * @param core the core to be compiled
     * @return boolean True if no errors where detected during the compilation
     */
    private void compileCore( Sketch sketch, Target target, Core core ) {

        //the command that will be run for compilation
        List<String> cmd = null;

        //check that the build directory for the core exists
        File outputDir = sketch.getCoreBuildRoot( target );
        if ( outputDir == null ) {
            getLog().error( "Compiling Core failed. Output directory `" + sketch.getBuildRootPath( target ) + File.separator + "core` does not exist or cannot be read or written" );
            throw new IllegalStateException();
        }

        List<String> baseC = new ArrayList<String>( cCmd );
        baseC.add( "-mmcu=" + target.getMCU() );
        baseC.add( "-DF_CPU=" + target.getSpeed() );
        baseC.add( "-I" + core.getDirectory().getPath() );

        List<String> baseCPP = new ArrayList<String>( cppCmd );
        baseCPP.add( "-mmcu=" + target.getMCU() );
        baseCPP.add( "-DF_CPU=" + target.getSpeed() );
        baseCPP.add( "-I" + core.getDirectory().getPath() );

        for (File f : core.getSourceFiles()) {
            if ( f.getName().toLowerCase().endsWith( ".c" ) ) {
                cmd = new ArrayList<String>( baseC );
            }
            else if ( f.getName().toLowerCase().endsWith( ".cpp" ) ) {
                cmd = new ArrayList<String>( baseCPP );
            }
            cmd.add( f.getPath() );
            cmd.add( "-o" + outputDir.getPath() + File.separator + f.getName() + ".o" );
            execute( cmd );
        }

        getLog().info( "Compiled Core `" + target.getCore() + "` target `" + target.getKey() + " = " + target.getName() + "`" );
    }
}
