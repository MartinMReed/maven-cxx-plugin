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

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String configuration;

    /**
     * @parameter
     */
    public String[] targetIncludes;

    /**
     * @parameter
     */
    public String[] targetExcludes;

    @Override
    public final void execute() {

        XCodeService.setConfiguration( configuration );

        if ( targetIncludes != null && targetIncludes.length > 0 ) {
            XCodeService.setTargetIncludes( targetIncludes );
        }

        if ( targetExcludes != null && targetExcludes.length > 0 ) {
            XCodeService.setTargetExcludes( targetExcludes );
        }
    }
}
