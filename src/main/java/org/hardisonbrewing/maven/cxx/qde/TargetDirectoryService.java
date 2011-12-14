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
package org.hardisonbrewing.maven.cxx.qde;

import java.io.File;

public final class TargetDirectoryService extends org.hardisonbrewing.maven.cxx.TargetDirectoryService {

    public static final String BAR_DESCRIPTOR_XML = "bar-descriptor.xml";

    private TargetDirectoryService() {

        // do nothing
    }

    public static String getBarDescriptorXmlPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( BAR_DESCRIPTOR_XML );
        return stringBuffer.toString();
    }

    public static File getBarDescriptorXmlFile() {

        return new File( getBarDescriptorXmlPath() );
    }
}
