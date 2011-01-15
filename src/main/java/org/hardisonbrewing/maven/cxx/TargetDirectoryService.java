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

import java.io.File;

import org.hardisonbrewing.maven.core.FileUtils;

public class TargetDirectoryService extends org.hardisonbrewing.maven.core.TargetDirectoryService {

    public static final String SOURCES_DIRECTORY = "generated-sources";
    public static final String RESOURCES_DIRECTORY = "generated-resources";

    protected TargetDirectoryService() {

        // do nothing
    }

    public static String getGeneratedSourcesDirectoryPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( SOURCES_DIRECTORY );
        return stringBuffer.toString();
    }

    public static File getGeneratedSourcesDirectory() {

        return new File( getGeneratedSourcesDirectoryPath() );
    }

    public static String getGeneratedResourcesDirectoryPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( RESOURCES_DIRECTORY );
        return stringBuffer.toString();
    }

    public static File getGeneratedResourcesDirectory() {

        return new File( getGeneratedResourcesDirectoryPath() );
    }

    public static final String[] getSourceFilePaths() {

        return FileUtils.listFilePathsRecursive( getGeneratedSourcesDirectory() );
    }

    public static final File[] getSourceFiles() {

        return FileUtils.listFilesRecursive( getGeneratedSourcesDirectory() );
    }

    public static final String[] getResourceFilePaths() {

        return FileUtils.listFilePathsRecursive( getGeneratedResourcesDirectory() );
    }

    public static final File[] getResourceFiles() {

        return FileUtils.listFilesRecursive( getGeneratedResourcesDirectory() );
    }
}
