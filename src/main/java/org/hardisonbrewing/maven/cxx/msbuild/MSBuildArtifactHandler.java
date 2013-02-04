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

import org.apache.maven.artifact.handler.DefaultArtifactHandler;

public final class MSBuildArtifactHandler extends DefaultArtifactHandler {

    @Override
    public String getExtension() {

        if ( !PropertiesService.hasXapOutput() ) {
            return "dll";
        }

        return MSBuildService.XAP_EXTENSION;
    }
}
