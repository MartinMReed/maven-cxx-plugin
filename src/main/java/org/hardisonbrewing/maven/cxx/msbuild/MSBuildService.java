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

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.ProjectService;

public class MSBuildService {

    public static final String XAP_EXTENSION = "xap";
    public static final String SLN_EXTENSION = "sln";
    public static final String CSPROJ_EXTENSION = "csproj";
    public static final String PROJ_EXTENSION = "proj";

    public static final String BUILD_XAP_FILENAME = "XapFilename";

    private static String project;

    protected MSBuildService() {

        // do nothing
    }

    public static File[] listSolutions() {

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { "*." + SLN_EXTENSION };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
    }

    public static File[] listProjects() {

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { "*." + CSPROJ_EXTENSION, "*." + PROJ_EXTENSION };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
    }

    public static String getProject() {

        return project;
    }

    public static void setProject( String project ) {

        MSBuildService.project = project;
    }
}
