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

package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.Properties;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.PropertiesService {

    private static Properties properties;

    protected PropertiesService() {

        // do nothing
    }

    public static final Properties getXCodeProperties() {

        if ( properties == null ) {
            properties = loadProperties( getXCodePropertiesPath() );
        }
        return properties;
    }

    public static final String getXCodePropertiesPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( XCodeService.getProject() );
        stringBuffer.append( ".pbxproj.properties" );
        return stringBuffer.toString();
    }

    public static final void storeXCodeProperties( Properties properties ) {

        storeProperties( properties, getXCodePropertiesPath() );
    }

    public static final String getXCodePropertiesKey( String prefix, String key ) {

        StringBuffer stringBuffer = new StringBuffer();
        if ( prefix != null ) {
            stringBuffer.append( prefix );
            stringBuffer.append( "." );
        }
        stringBuffer.append( key );
        return stringBuffer.toString();
    }

    public static final String getXCodeProperty( String key ) {

        return (String) getXCodeProperties().get( key );
    }

    public static final String getXCodeProperty( String prefix, String key ) {

        key = getXCodePropertiesKey( prefix, key );
        return (String) getXCodeProperties().get( key );
    }

    public static final String getTargetProductName( String target ) {

        return getXCodeProperty( target, "productReference" );
    }
}
