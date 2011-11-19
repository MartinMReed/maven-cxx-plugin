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
package org.hardisonbrewing.maven.cxx.bar;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PropertiesService extends org.hardisonbrewing.maven.cxx.PropertiesService {

    public static final String ADOBE_FLEX_HOME;
    public static final String BLACKBERRY_WEBWORKS_TABLET_HOME;
    public static final String BLACKBERRY_TABLET_HOME;
    public static final String BLACKBERRY_TABLET_DEVICE_IP;
    public static final String BLACKBERRY_TABLET_DEVICE_PASSWORD;
    public static final String DEBUG;

    private static final String[] properties;

    static {

        List<String> keys = new LinkedList<String>();
        keys.add( ADOBE_FLEX_HOME = "adobe.flex.home" );
        keys.add( BLACKBERRY_WEBWORKS_TABLET_HOME = "blackberry.webworks.tablet.home" );
        keys.add( BLACKBERRY_TABLET_HOME = "blackberry.tablet.home" );
        keys.add( BLACKBERRY_TABLET_DEVICE_IP = "blackberry.tablet.device.ip" );
        keys.add( BLACKBERRY_TABLET_DEVICE_PASSWORD = "blackberry.tablet.device.password" );
        keys.add( DEBUG = "debug" );

        properties = new String[keys.size()];
        keys.toArray( properties );
    }

    protected PropertiesService() {

        // do nothing
    }

    public static final boolean propertiesHaveChanged() {

        if ( pluginVersionsHaveChanged() ) {
            return true;
        }

        Properties properties = loadBuildDifferenceProperties();
        if ( properties == null ) {
            return false;
        }

        for (String key : PropertiesService.properties) {
            if ( "true".equalsIgnoreCase( (String) properties.get( key ) ) ) {
                return true;
            }
        }

        return false;
    }
}
