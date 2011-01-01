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

import org.hardisonbrewing.maven.core.TargetDirectoryService;

/**
 * @goal xcode-prepare-package
 * @phase prepare-package
 * @requiresDependencyResolution prepare-package
 */
public final class PreparePackageMojo extends org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo {

    /**
     * @parameter expression="${configuration.project}"
     */
    public String project;

    @Override
    protected void prepareArtifact() {

        StringBuffer buildDirPath = new StringBuffer();
        buildDirPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        buildDirPath.append( File.separator );
        buildDirPath.append( "build" );
        File buildDir = new File( buildDirPath.toString() );

        String projectBuildDir = project + ".build";

        for (String buildDirChild : buildDir.list()) {
            if ( projectBuildDir.equals( buildDirChild ) ) {
                continue;
            }
            StringBuffer releaseDirPath = new StringBuffer();
            releaseDirPath.append( buildDir );
            releaseDirPath.append( File.separator );
            releaseDirPath.append( buildDirChild );
            File releaseDir = new File( releaseDirPath.toString() );
            if ( !releaseDir.isDirectory() ) {
                continue;
            }
            for (File releaseFile : releaseDir.listFiles()) {
                prepareTargetFile( releaseFile, releaseFile.getName() );
            }
        }
    }
}
