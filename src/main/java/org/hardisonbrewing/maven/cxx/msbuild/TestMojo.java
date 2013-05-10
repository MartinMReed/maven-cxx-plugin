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
package org.hardisonbrewing.maven.cxx.msbuild;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.LogStreamConsumer;

/**
 * @goal msbuild-test
 * @phase test
 */
public final class TestMojo extends JoJoMojoImpl {

    /**
     * @parameter default-value="${maven.test.skip}"
     */
    public boolean skipTests;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if ( skipTests ) {
            getLog().info( "Tests disabled, skipping." );
            return;
        }

        String testProjectType = PropertiesService.getBuildSetting( MSBuildService.BUILD_TEST_PROJECT_TYPE );

        if ( testProjectType == null || testProjectType.length() == 0 ) {
            getLog().info( "Not a test project, skipping." );
            return;
        }

        if ( !testProjectType.equals( "UnitTest" ) ) {
            getLog().info( "Not a supported test project, skipping." );
            return;
        }

        shutdownCoverage();
        killMonitorTask();

        try {
            instrumentFiles();
            startCoverage();
            executeTest();
        }
        finally {
            shutdownCoverage();
            killMonitorTask();
        }
    }

    private void startCoverage() {

        File file = TargetDirectoryService.getTestCoverageFile();

        List<String> cmd = new LinkedList<String>();
        cmd.add( "vsperfcmd" );
        cmd.add( "/start:coverage" );
        cmd.add( "/output:" + file.getName() );

        final Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );
        commandLine.setWorkingDirectory( file.getParent() );

        boolean[] monitorLock = new boolean[1];
        final StreamConsumer systemOut = new MyLogStreamConsumer( monitorLock, LogStreamConsumer.LEVEL_INFO );
        final StreamConsumer systemErr = new MyLogStreamConsumer( monitorLock, LogStreamConsumer.LEVEL_ERROR );

        new Thread() {

            @Override
            public void run() {

                execute( commandLine, systemOut, systemErr );
            }
        }.start();

        synchronized (monitorLock) {
            while (!monitorLock[0]) {
                try {
                    monitorLock.wait();
                }
                catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    private static final class MyLogStreamConsumer extends LogStreamConsumer {

        private final boolean[] monitorLock;

        public MyLogStreamConsumer(boolean[] monitorLock, int level) {

            super( level );

            this.monitorLock = monitorLock;
        }

        @Override
        public void consumeLine( String line ) {

            if ( !monitorLock[0] ) {
                synchronized (monitorLock) {
                    if ( !monitorLock[0] ) {
                        monitorLock[0] = true;
                        monitorLock.notify();
                    }
                }
            }

            super.consumeLine( line );
        }
    }

    private void instrumentFiles() {

        File binDir = new File( TargetDirectoryService.getBinDirectoryPath() );
        String[] filePaths = FileUtils.listFilePathsRecursive( binDir, new String[] { "**/*.dll" }, null );

        for (String filePath : filePaths) {

            String assemblyName = filePath.substring( 0, filePath.length() - "dll".length() );
            String pdbFilePath = assemblyName + "pdb";

            if ( !FileUtils.exists( pdbFilePath ) ) {
                continue;
            }

            List<String> cmd = new LinkedList<String>();
            cmd.add( "vsinstr" );
            cmd.add( "/coverage" );
            cmd.add( filePath );

            Commandline commandLine = buildCommandline( cmd );
            CommandLineService.addDotnetEnvVars( commandLine );
            execute( commandLine );
        }
    }

    private void executeTest() {

        StringBuffer filePath = new StringBuffer();
        filePath.append( TargetDirectoryService.getBinDirectoryPath() );
        filePath.append( File.separator );
        filePath.append( PropertiesService.getBuildSetting( MSBuildService.BUILD_ASSEMBLY_NAME ) );
        filePath.append( ".dll" );
        File file = new File( filePath.toString() );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "mstest" );
        cmd.add( "/testcontainer:" + file );
        cmd.add( "/resultsfile:" + TargetDirectoryService.getTestResultPath() );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addDotnetEnvVars( commandLine );
        execute( commandLine );
    }

    private void shutdownCoverage() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "vsperfcmd" );
        cmd.add( "/shutdown" );

        try {
            Commandline commandLine = buildCommandline( cmd );
            CommandLineService.addDotnetEnvVars( commandLine );
            execute( commandLine );
        }
        catch (Exception e) {
            // do nothing
        }
    }

    private void killMonitorTask() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "taskkill" );
        cmd.add( "/F" );
        cmd.add( "/IM" );
        cmd.add( "vsperfmon.exe" );

        try {
            Commandline commandLine = buildCommandline( cmd );
            execute( commandLine );
        }
        catch (Exception e) {
            // do nothing
        }
    }
}
