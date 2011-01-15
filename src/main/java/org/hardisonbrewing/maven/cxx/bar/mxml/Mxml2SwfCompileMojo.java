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

package org.hardisonbrewing.maven.cxx.bar.mxml;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.bar.AbstractSwfCompileMojo;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;
import org.hardisonbrewing.maven.cxx.bar.TargetDirectoryService;

/**
 * @goal mxml-bar-swf-compile
 * @phase compile
 */
public class Mxml2SwfCompileMojo extends AbstractSwfCompileMojo {

    @Override
    public void execute() {

        String artifactId = getProject().getArtifactId();

        if ( !shouldExecute() ) {
            getLog().info( artifactId + ".swf is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Building " + artifactId + ".swf..." );

        List<String> cmd = new LinkedList<String>();
        cmd.add( "mxmlc" );

        cmd.add( "-output" );
        cmd.add( artifactId + ".swf" );

        if ( PropertiesService.getPropertyAsBoolean( PropertiesService.DEBUG ) ) {
            cmd.add( "-compiler.debug" );
        }

        StringBuffer actionScriptPath = new StringBuffer();
        actionScriptPath.append( TargetDirectoryService.getGeneratedSourcesDirectoryPath() );
        actionScriptPath.append( File.separator );
        actionScriptPath.append( artifactId );
        actionScriptPath.append( ".mxml" );
        cmd.add( actionScriptPath.toString() );

        File sdkHome = PropertiesService.getPropertyAsFile( PropertiesService.ADOBE_FLEX_HOME );

        StringBuffer configPath = new StringBuffer();
        configPath.append( sdkHome );
        configPath.append( File.separator );
        configPath.append( "frameworks" );
        configPath.append( File.separator );
        configPath.append( "airmobile-config.xml" );
        cmd.add( "-load-config" );
        cmd.add( configPath.toString() );

        cmd.add( "-include-libraries+=" + artifactId + ".swc" );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }
}
