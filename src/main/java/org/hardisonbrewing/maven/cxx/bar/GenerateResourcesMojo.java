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

import org.hardisonbrewing.maven.cxx.generic.GenerateSourcesMojo;

/**
 * @goal bar-generate-resources
 * @phase generate-resources
 */
public class GenerateResourcesMojo extends org.hardisonbrewing.maven.cxx.generic.GenerateResourcesMojo {

    protected void copyFile( String srcFilePath, String filePathPrefix, String destDirectoryPath ) {

        StringBuffer tabletXmlPath = new StringBuffer();
        tabletXmlPath.append( filePathPrefix );
        tabletXmlPath.append( File.separator );
        tabletXmlPath.append( TargetDirectoryService.APP_DESCRIPTOR_FILENAME );
        if ( srcFilePath.equals( tabletXmlPath.toString() ) ) {
            destDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();
        }
        GenerateSourcesMojo.copyFile( srcFilePath, filePathPrefix, destDirectoryPath );
    }
}
