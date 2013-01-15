/**
 * Copyright (c) 2010-2013 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.generic;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.DependencyService;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal install
 * @phase install
 */
public final class InstallMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String classifier;

    protected InstallMojo() {

        // do nothing
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File src = new File( TargetDirectoryService.getTempPackagePath() + ".zip" );
        Artifact artifact = DependencyService.createArtifactWithClassifier( getProject().getArtifact(), classifier );

        try {
            DependencyService.install( src, artifact );
        }
        catch (ArtifactInstallationException e) {
            throw new IllegalStateException( e );
        }
    }
}
