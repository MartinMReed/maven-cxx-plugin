/**
 * Copyright (c) 2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.flex;

import java.io.File;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.PropertiesService {

    private static Properties properties;

    protected PropertiesService() {

        // do nothing
    }

    public static final Properties getFlexProperties() {

        if ( properties == null ) {
            properties = loadProperties( getFlexPropertiesPath() );
        }
        if ( properties == null ) {
            Properties properties = new Properties();
            storeFlexProperties( properties );
            PropertiesService.properties = properties;
        }
        return properties;
    }

    public static final String getFlexPropertiesPath() {

        MavenProject project = JoJoMojo.getMojo().getProject();
        Artifact artifact = project.getArtifact();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( artifact.getArtifactId() );
        stringBuffer.append( ".flex.properties" );
        return stringBuffer.toString();
    }

    public static final void storeFlexProperties( Properties properties ) {

        storeProperties( properties, getFlexPropertiesPath() );
    }

    public static final String getFlexPropertiesKey( String prefix, String key ) {

        StringBuffer stringBuffer = new StringBuffer();
        if ( prefix != null ) {
            stringBuffer.append( prefix );
            stringBuffer.append( "." );
        }
        stringBuffer.append( key );
        return stringBuffer.toString();
    }

    public static final String getFlexProperty( String key ) {

        return (String) getFlexProperties().get( key );
    }

    public static final String getFlexProperty( String prefix, String key ) {

        key = getFlexPropertiesKey( prefix, key );
        return (String) getFlexProperties().get( key );
    }
}
