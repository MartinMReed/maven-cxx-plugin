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
import java.util.Properties;

import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

public class PropertiesService extends org.hardisonbrewing.maven.core.PropertiesService {

    protected PropertiesService() {

        // do nothing
    }

    public static final void storeBuildDifferenceProperties( Properties properties ) {

        StringBuffer filePath = new StringBuffer();
        filePath.append( TargetDirectoryService.getTargetDirectoryPath() );
        filePath.append( File.separator );
        filePath.append( "build_difference.properties" );
        storeProperties( properties, filePath.toString() );
    }

    public static final void storeBuildProperties() {

        Properties properties = PropertiesService.getProperties();

        StringBuffer filePath = new StringBuffer();
        filePath.append( TargetDirectoryService.getTargetDirectoryPath() );
        filePath.append( File.separator );
        filePath.append( "build.properties" );
        storeProperties( properties, filePath.toString() );
    }

    public static final Properties loadBuildDifferenceProperties() {

        StringBuffer filePath = new StringBuffer();
        filePath.append( TargetDirectoryService.getTargetDirectoryPath() );
        filePath.append( File.separator );
        filePath.append( "build_difference.properties" );
        return loadProperties( filePath.toString() );
    }

    public static final Properties loadBuildProperties() {

        StringBuffer filePath = new StringBuffer();
        filePath.append( TargetDirectoryService.getTargetDirectoryPath() );
        filePath.append( File.separator );
        filePath.append( "build.properties" );
        return loadProperties( filePath.toString() );
    }
}
