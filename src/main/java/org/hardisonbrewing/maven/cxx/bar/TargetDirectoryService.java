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

import java.io.File;

public final class TargetDirectoryService extends org.hardisonbrewing.maven.cxx.TargetDirectoryService {

    public static final String BLACKBERRY_TABLET_XML = "blackberry-tablet.xml";

    private TargetDirectoryService() {

        // do nothing
    }

    public static String getTabletXmlPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( BLACKBERRY_TABLET_XML );
        return stringBuffer.toString();
    }

    public static File getTabletXmlFile() {

        return new File( getTabletXmlPath() );
    }
}
