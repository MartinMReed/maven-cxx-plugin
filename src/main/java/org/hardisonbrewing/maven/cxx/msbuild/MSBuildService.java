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
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;

public class MSBuildService {

    public static final String XAP_EXTENSION = "xap";
    public static final String SLN_EXTENSION = "sln";
    public static final String CSPROJ_EXTENSION = "csproj";
    public static final String PROJ_EXTENSION = "proj";

    public static final String BUILD_ASSEMBLY_NAME = "AssemblyName";
    public static final String BUILD_CONFIGURATION = "Configuration";
    public static final String BUILD_XAP_FILENAME = "XapFilename";
    public static final String BUILD_XAP_OUTPUTS = "XapOutputs";

    public static final String ASSEMBLY_INFO_CS = "AssemblyInfo.cs";

    private static String projectFilePath;

    protected MSBuildService() {

        // do nothing
    }

    public static File[] listProjects() {

        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { "*." + CSPROJ_EXTENSION, "*." + PROJ_EXTENSION };
        return FileUtils.listFilesRecursive( baseDir, includes, null );
    }

    public static String findProjectFilePath( String project ) {

        if ( project != null ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( project );
            return stringBuffer.toString();
        }

        File[] projects = listProjects();

        if ( projects != null && projects.length > 0 ) {

            if ( projects.length > 1 ) {
                JoJoMojo.getMojo().getLog().error( "Multiple project files available. Please specify a <project/> in the pom.xml" );
                throw new IllegalStateException();
            }

            return projects[0].getPath();
        }

        JoJoMojo.getMojo().getLog().error( "Unable to determine the project file. Please specify a <project/> in the pom.xml" );
        throw new IllegalStateException();
    }

    public static final File getAssemblyInfoFile( String project ) {

        String projectFilePath = getProjectFilePath();

        // can be null if assembly info mojo is called directly
        if ( projectFilePath == null ) {
            projectFilePath = findProjectFilePath( project );
        }

        File projectFile = new File( projectFilePath );

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( projectFile.getParent() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "Properties" );
        stringBuffer.append( File.separator );
        stringBuffer.append( ASSEMBLY_INFO_CS );
        return new File( stringBuffer.toString() );
    }

    public static String getProjectFilePath() {

        return projectFilePath;
    }

    public static void setProjectFilePath( String projectFilePath ) {

        MSBuildService.projectFilePath = projectFilePath;
    }
}
