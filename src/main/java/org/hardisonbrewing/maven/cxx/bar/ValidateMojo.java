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

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal bar-validate
 * @phase validate
 */
public class ValidateMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        validateVersion();

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_TABLET_HOME, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_TABLET_DEVICE_IP, false );
        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_TABLET_DEVICE_PASSWORD, false );
    }

    private final int isSnapshotVersion( String version ) {

        String suffix = "-SNAPSHOT";
        if ( version.endsWith( suffix ) ) {
            int indexOf = version.indexOf( suffix );
            if ( indexOf != -1 && indexOf == version.lastIndexOf( suffix ) ) {
                return indexOf;
            }
        }
        return -1;
    }

    private final void validateVersion() {

        String version = getProject().getVersion();
        getLog().info( "Validating version: " + version );

        int snapshot = isSnapshotVersion( version );
        if ( snapshot != -1 ) {
            version = version.substring( 0, snapshot );
        }

        if ( version.length() == 0 ) {
            throwInvalidVersion();
            return;
        }

        String[] parts = version.split( "\\." );
        if ( parts.length == 0 || parts.length > 3 ) {
            throwInvalidVersion();
            return;
        }

        for (String part : parts) {
            int i;
            try {
                i = Integer.parseInt( part );
            }
            catch (Exception e) {
                i = -1;
            }
            if ( i < 0 || i > 999 ) {
                throwInvalidVersion();
                return;
            }
        }
    }

    private final void throwInvalidVersion() {

        String version = getProject().getVersion();
        getLog().error( "Invalid version[" + version + "]! Must be of format: <0-999>.<0-999>.<0-999>[-SNAPSHOT]" );
        throw new IllegalArgumentException();
    }
}
