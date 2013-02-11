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

import org.apache.maven.artifact.handler.ArtifactHandler;

public final class MSBuildArtifactHandler implements ArtifactHandler {

    private final ArtifactHandler artifactHandler;
    private String extension;

    public MSBuildArtifactHandler(ArtifactHandler artifactHandler) {

        this.artifactHandler = artifactHandler;
    }

    @Override
    public String getClassifier() {

        return artifactHandler.getClassifier();
    }

    @Override
    public String getDirectory() {

        return artifactHandler.getDirectory();
    }

    @Override
    public String getExtension() {

        if ( extension != null ) {
            return extension;
        }

        return artifactHandler.getExtension();
    }

    public void setExtension( String extension ) {

        this.extension = extension;
    }

    @Override
    public String getLanguage() {

        return artifactHandler.getLanguage();
    }

    @Override
    public String getPackaging() {

        return artifactHandler.getPackaging();
    }

    @Override
    public boolean isAddedToClasspath() {

        return artifactHandler.isAddedToClasspath();
    }

    @Override
    public boolean isIncludesDependencies() {

        return artifactHandler.isIncludesDependencies();
    }
}
