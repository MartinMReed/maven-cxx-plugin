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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal msbuild-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String project;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if ( project == null ) {
            project = findProjectFile();
        }

        MSBuildService.setProject( project );
    }

    private String findProjectFile() {

        File[] solutions = MSBuildService.listSolutions();

        if ( solutions != null && solutions.length > 0 ) {

            if ( solutions.length > 1 ) {
                getLog().error( "Multiple project solution files available. Please specify a <project/> in the pom.xml" );
                throw new IllegalStateException();
            }

            return solutions[0].getName();
        }

        File[] projects = MSBuildService.listProjects();

        if ( projects != null && projects.length > 0 ) {

            if ( projects.length > 1 ) {
                getLog().error( "Multiple project files available. Please specify a <project/> in the pom.xml" );
                throw new IllegalStateException();
            }

            return projects[0].getName();
        }

        getLog().error( "Unable to determine the project file. Please specify a <project/> in the pom.xml" );
        throw new IllegalStateException();
    }
}
