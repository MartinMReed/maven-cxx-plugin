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
}
