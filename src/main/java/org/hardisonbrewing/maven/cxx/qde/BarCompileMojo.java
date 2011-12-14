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
package org.hardisonbrewing.maven.cxx.qde;

import generated.org.eclipse.cdt.Cproject;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.bar.LaunchMojo;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal qde-bar-compile
 * @phase compile
 */
public class BarCompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        Cproject cproject = CProjectService.getCProject();
        String projectName = CProjectService.getProjectName( cproject );

        getLog().info( "Building " + projectName + ".bar..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "blackberry-nativepackager" );

        // this is only here to ensure the entry point in the Manifest shows as AIRDebug
        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-connect" );
            cmd.add( LaunchMojo.getIpAddress() );
        }

        cmd.add( "-package" );
        cmd.add( projectName + ".bar" );

        cmd.add( TargetDirectoryService.getBarDescriptorXmlPath() );

        //        cmd.add( "-e" );
        //        cmd.add( TargetDirectoryService.RESOURCES_DIRECTORY );
        //        cmd.add( "." );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.addQnxEnvVars( commandLine );
        execute( commandLine );
    }
}
