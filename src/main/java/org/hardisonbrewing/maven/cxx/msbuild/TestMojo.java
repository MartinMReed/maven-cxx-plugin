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
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

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
}
