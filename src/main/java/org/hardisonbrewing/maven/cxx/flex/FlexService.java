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
import java.util.List;

import org.hardisonbrewing.maven.core.ProjectService;

public class FlexService {

    public static final String[] NON_RESOURCE_EXTS = new String[] { "**/*.as", "**/*.mxml" };

    public static final String IOS_TARGET_EXT = "ipa";
    public static final String ANDROID_TARGET_EXT = "apk";
    public static final String AIR_TARGET_EXT = "air";

    public static final boolean isAirTarget( String target ) {

        return AIR_TARGET_EXT.equals( target );
    }

    public static final boolean isIosTarget( String target ) {

        return target.startsWith( IOS_TARGET_EXT );
    }

    public static final boolean isAndroidTarget( String target ) {

        return target.startsWith( ANDROID_TARGET_EXT );
    }

    public static void addConfig( List<String> cmd, String config, String target ) {

        String configPath = config;

        if ( configPath == null || configPath.length() == 0 ) {

            String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( sdkHome );
            stringBuffer.append( File.separator );
            stringBuffer.append( "frameworks" );
            stringBuffer.append( File.separator );
            if ( FlexService.isIosTarget( target ) || FlexService.isAndroidTarget( target ) ) {
                stringBuffer.append( "airmobile-config.xml" );
            }
            else {
                stringBuffer.append( "air-config.xml" );
            }
            configPath = stringBuffer.toString();
        }
        else if ( !configPath.startsWith( File.separator ) ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( configPath );
            configPath = stringBuffer.toString();
        }
        cmd.add( "-load-config" );
        cmd.add( configPath );
    }
}
