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
package org.hardisonbrewing.maven.cxx.msbuild;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal msbuild-package
 * @phase package
 */
public final class PackageMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String classifier;

    /**
     * @component
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        File file;

        if ( !PropertiesService.hasXapOutput() ) {
            StringBuffer filePath = new StringBuffer();
            filePath.append( TargetDirectoryService.getBinDirectoryPath() );
            filePath.append( File.separator );
            filePath.append( PropertiesService.getBuildSetting( MSBuildService.BUILD_ASSEMBLY_NAME ) );
            filePath.append( ".dll" );
            file = new File( filePath.toString() );
        }
        else {
            StringBuffer filePath = new StringBuffer();
            filePath.append( TargetDirectoryService.getBinDirectoryPath() );
            filePath.append( File.separator );
            filePath.append( PropertiesService. getBuildSetting( MSBuildService.BUILD_XAP_FILENAME ) );
            file = new File( filePath.toString() );
        }

        MavenProject project = getProject();

        if ( classifier == null ) {
            project.getArtifact().setFile( file );
        }
        else {
            projectHelper.attachArtifact( project, file, classifier );
        }
    }
}
