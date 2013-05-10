/**
 * Copyright (c) 2013 Martin M Reed
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

import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal xcode-start-simulator
 * @phase prepare-package
 */
public final class StartSimulatorMojo extends AbstractSimulatorMojo {

    private static final String XCODE_SIMULATOR_DIR = /*/Applications/Xcode.app*/"/Contents/Applications/iPhone Simulator.app";

    /**
     * @parameter
     */
    public String simulatorSdk;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( skipTests ) {
            getLog().info( "Tests disabled, skipping." );
            return;
        }

        if ( !testOnSimulator ) {
            getLog().info( "Test on simulator disabled, skipping." );
            return;
        }

        killSimulator();

        if ( simulatorSdk != null ) {
            setDefaultSimulator( simulatorSdk );
        }

        runSimulator();
    }

    private void setDefaultSimulator( String version ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "defaults" );
        cmd.add( "write" );
        cmd.add( "com.apple.iphonesimulator" );
        cmd.add( "'currentSDKRoot'" );
        cmd.add( XCodeService.getSimulatorSdkPath( version ) );

        execute( cmd );
    }

    private void runSimulator() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( XCodeService.getXcodePath() );
        stringBuffer.append( XCODE_SIMULATOR_DIR );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "open" );
        cmd.add( stringBuffer.toString() );

        execute( cmd );
    }
}
