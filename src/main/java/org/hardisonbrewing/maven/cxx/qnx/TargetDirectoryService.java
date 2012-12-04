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
package org.hardisonbrewing.maven.cxx.qnx;

import generated.net.rim.bar.BarDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.ProjectService;

public class TargetDirectoryService extends org.hardisonbrewing.maven.cxx.TargetDirectoryService {

    public static final String getBarPath( BarDescriptor barDescriptor ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( barDescriptor.getName() );
        stringBuffer.append( ".bar" );
        return stringBuffer.toString();
    }

    public static String[] getMakefileDirectoryPaths() {

        return FileUtils.convertToPaths( getMakefileDirectories() );
    }

    public static File[] getMakefileDirectories() {

        StringBuffer extensionInclude = new StringBuffer();
        extensionInclude.append( "**" );
        extensionInclude.append( File.separator );
        extensionInclude.append( "Makefile" );
        
        File baseDir = ProjectService.getBaseDir();
        String[] includes = new String[] { extensionInclude.toString() };
        File[] makefiles = FileUtils.listFilesRecursive( baseDir, includes, null );

        List<File> directories = new ArrayList<File>();

        for (File makefile : makefiles) {

            File parent = makefile.getParentFile();

            String[] subdirectories = FileUtils.listDirectoryPathsRecursive( parent );
            if ( subdirectories.length > 0 ) {
                continue;
            }

            directories.add( parent );
        }

        File[] _directories = new File[directories.size()];
        directories.toArray( _directories );
        return _directories;
    }
}
