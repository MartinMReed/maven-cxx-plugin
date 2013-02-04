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
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.PropertiesService {

    public static final String DOTNET_FRAMEWORK_HOME = "dotnet.framework.home";

    protected PropertiesService() {

        // do nothing
    }

    public static final String getBuildXapFilename() {

        return getBuildSetting( MSBuildService.BUILD_XAP_FILENAME );
    }

    public static final boolean hasXapOutput() {

        String output = getBuildSetting( MSBuildService.BUILD_XAP_OUTPUTS );
        return "true".equalsIgnoreCase( output );
    }

    public static final String getBuildSetting( String key ) {

        Properties properties = PropertiesService.getBuildSettings();
        return properties.getProperty( key );
    }

    public static final Properties getBuildSettings() {

        return loadProperties( getBuildSettingsPath() );
    }

    public static final String getBuildSettingsPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "buildSettings.properties" );
        return stringBuffer.toString();
    }

    public static final void storeBuildSettings( Properties buildSettings ) {

        String filePath = getBuildSettingsPath();
        FileUtils.ensureParentExists( filePath );
        storeProperties( buildSettings, filePath );
    }
}
