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

package org.hardisonbrewing.maven.cxx;

import org.hardisonbrewing.maven.cxx.generic.Sources;

public final class ProjectService extends org.hardisonbrewing.maven.core.ProjectService {

    private static Sources sources;

    private ProjectService() {

        // do nothing
    }

    public static final String[] getSourceFilePaths() {

        String[] sourceFilePaths = org.hardisonbrewing.maven.core.ProjectService.getSourceFilePaths();

        return sourceFilePaths;
    }

    public static Sources getSources() {

        return sources;
    }

    public static void setSources( Sources sources ) {

        ProjectService.sources = sources;
    }
}
