/**
 * Copyright (c) 2010 Martin M Reed
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
import org.hardisonbrewing.maven.core.DependencyService;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TargetDirectoryService;

public abstract class InstallMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    protected String classifier;

    /**
     * @parameter expression="${configuration.classifier}"
     */
    private String projectClassifier;

    protected InstallMojo() {

        // do nothing
    }

    @Override
    public void execute() {

        String classifier = this.projectClassifier;
        if ( classifier == null ) {
            classifier = this.classifier;
        }

        if ( classifier == null ) {
            throw new IllegalStateException( "Classifier could not be determined. Please specify manually as <classifier />." );
        }

        File src = new File( TargetDirectoryService.getTempPackagePath() + ".jar" );
        Artifact artifact = DependencyService.createArtifactWithClassifier( getProject().getArtifact(), classifier );

        try {
            DependencyService.install( src, artifact );
        }
        catch (ArtifactInstallationException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
