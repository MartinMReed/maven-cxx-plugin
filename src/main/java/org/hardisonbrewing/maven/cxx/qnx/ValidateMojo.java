/**
 * Copyright (c) 2011 Martin M Reed
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

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal qnx-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    @Override
    public final void execute() {

        File file = QnxService.getCProjectFile();
        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate .cproject file:" + file );
            throw new IllegalStateException();
        }
    }
}
