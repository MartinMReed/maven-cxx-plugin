/**
 * Copyright (c) 2010 Martin M Reed
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
 * @goal xcode-install
 * @phase install
 * @requiresDependencyResolution install
 */
public final class InstallMojo extends org.hardisonbrewing.maven.cxx.InstallMojo {

    /**
     * @parameter
     */
    public String[] includes;

    @Override
    public final void execute() {

        File tempPackage = new File( TargetDirectoryService.getTempPackagePath() );
        for (File tempPackageChild : tempPackage.listFiles()) {
            if ( tempPackageChild.isDirectory() ) {
                continue;
            }
            String tempPackageChildName = tempPackageChild.getName();
            if ( isIncluded( tempPackageChildName ) ) {
                continue;
            }
            classifier = tempPackageChildName.substring( tempPackageChildName.lastIndexOf( '.' ) + 1 );
        }

        super.execute();
    }

    private final boolean isIncluded( String fileName ) {

        if ( includes == null ) {
            return false;
        }
        for (int i = 0; i < includes.length; i++) {
            if ( includes[i].equals( fileName ) ) {
                return true;
            }
        }
        return false;
    }
}
