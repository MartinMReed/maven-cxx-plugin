/**
 * Copyright (c) 2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.flex;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal flex-prepare-package
 * @phase prepare-package
 */
public final class PreparePackageMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String target;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactId = getProject().getArtifactId();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( artifactId );
        stringBuffer.append( "." );
        if ( FlexService.isIosTarget( target ) ) {
            stringBuffer.append( FlexService.IOS_TARGET_EXT );
        }
        else if ( FlexService.isAndroidTarget( target ) ) {
            stringBuffer.append( FlexService.ANDROID_TARGET_EXT );
        }
        else {
            stringBuffer.append( FlexService.AIR_TARGET_EXT );
        }

        org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( stringBuffer.toString() );
    }
}
