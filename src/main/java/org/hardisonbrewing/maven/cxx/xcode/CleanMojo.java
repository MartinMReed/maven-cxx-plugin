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
import java.io.IOException;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;

/**
 * @goal xcode-clean
 * @phase clean
 */
public final class CleanMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        StringBuffer buildDirPath = new StringBuffer();
        buildDirPath.append( ProjectService.getBaseDirPath() );
        buildDirPath.append( File.separator );
        buildDirPath.append( "build" );
        File buildDir = new File( buildDirPath.toString() );
        try {
            FileUtils.deleteDirectory( buildDir );
            //            FileUtils.deleteDirectory( TargetDirectoryService.getTargetDirectory() );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
