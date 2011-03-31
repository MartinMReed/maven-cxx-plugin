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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-generate-info-plist
 * @phase xcode-generate-info-plist
 */
public class GenerateInfoPlistMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        //        Plist plist = XCodeService.readInfoPlist();
        //        if ( plist == null ) {
        //            getLog().info( "No Info.plist found... skipping" );
        //            return;
        //        }
        //
        //        InfoPlistService.setString( plist, "CFBundleName", getProject().getName() );
        //        InfoPlistService.setString( plist, "CFBundleVersion", XCodeService.getBundleVersion() );
        //        InfoPlistService.setString( plist, "CFBundleShortVersionString", getProject().getVersion() );
        //        InfoPlistService.setString( plist, "CFBundleIdentifier", XCodeService.getBundleIdentifier() );
        //        XCodeService.writeInfoPlist( plist );
    }
}
