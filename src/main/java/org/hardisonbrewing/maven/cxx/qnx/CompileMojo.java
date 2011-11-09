/**
 * Copyright (c) 2011 Martin M Reed
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

package org.hardisonbrewing.maven.cxx.qnx;

import generated.org.eclipse.cdt.ToolChain;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal o-qnx-compile
 * @phase compile
 */
public class CompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info( "QNX dir: " + QnxService.getQnxDirPath() );

        ToolChain toolChain = QnxService.getToolChain( target );

        getLog().info( "CPU: " + QnxService.getCpu( toolChain ) );

        getLog().info( "Compiler Defines:" );
        for (String value : QnxService.getCompilerDefines( toolChain )) {
            getLog().info( "    " + value );
        }

        getLog().info( "Compiler Include Paths:" );
        for (String value : QnxService.getCompilerIncludePaths( toolChain )) {
            value = PropertiesService.populateTemplateVariables( value, "${", "}" );
            File file = new File( value );
            getLog().info( "    " + value + "... " + ( file.exists() ? "valid" : "invalid" ) );
        }

        getLog().info( "Linker Libraries:" );
        for (String value : QnxService.getLinkerLibraries( toolChain )) {
            getLog().info( "    " + value );
        }

        getLog().info( "Linker Library Paths:" );
        for (String value : QnxService.getLinkerLibraryPaths( toolChain )) {
            value = PropertiesService.populateTemplateVariables( value, "${", "}" );
            File file = new File( value );
            getLog().info( "    " + value + "... " + ( file.exists() ? "valid" : "invalid" ) );
        }
    }
}
