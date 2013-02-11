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
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;

/**
 * @goal msbuild-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String project;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkConfigurationExists( "project", project, false );

        if ( project != null ) {
            validateProjectFile( project );
        }

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.DOTNET_FRAMEWORK_HOME, true );
    }

    private void validateProjectFile( String project ) {

        if ( !project.endsWith( MSBuildService.PROJ_EXTENSION ) || !project.endsWith( MSBuildService.CSPROJ_EXTENSION ) ) {
            getLog().error( "Project must be an extension of: " + MSBuildService.PROJ_EXTENSION + " or " + MSBuildService.CSPROJ_EXTENSION );
            throw new IllegalStateException();
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( project );
        String projectFilePath = stringBuffer.toString();

        if ( !FileUtils.exists( projectFilePath ) ) {
            getLog().error( "Project file not found: " + projectFilePath );
            throw new IllegalStateException();
        }
    }
}
