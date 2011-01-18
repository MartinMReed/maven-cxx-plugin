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

package org.hardisonbrewing.maven.cxx.bar.flex;

import java.io.File;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

public abstract class AbstractSwfCompileMojo extends JoJoMojoImpl {

    protected final boolean shouldExecute() {

        String swfFileName = getProject().getArtifactId() + ".swf";

        if ( PropertiesService.propertiesHaveChanged() ) {
            getLog().info( "Properties have changed, rebuilding " + swfFileName + "..." );
            return true;
        }

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( swfFileName );

        File outputFile = new File( outputPath.toString() );
        if ( outputFile.exists() ) {
            if ( outputFile.lastModified() >= getLatestFileDate() ) {
                return false;
            }
        }

        return true;
    }

    private final long getLatestFileDate() {

        long lastModified = 0;
        for (String filePath : TargetDirectoryService.getSourceFilePaths()) {
            lastModified = Math.max( lastModified, getLatestFileDate( filePath ) );
        }
        for (String filePath : TargetDirectoryService.getResourceFilePaths()) {
            lastModified = Math.max( lastModified, getLatestFileDate( filePath ) );
        }
        return lastModified;
    }

    private final long getLatestFileDate( String filePath ) {

        File sourceFile = new File( filePath );
        return FileUtils.lastModified( sourceFile, false );
    }
}
