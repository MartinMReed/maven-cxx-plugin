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

import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal qnx-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() {

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_NDK_HOME, true );

        File cproject = QnxService.getCProjectFile();
        if ( !cproject.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate .cproject file: " + cproject );
            throw new IllegalStateException();
        }

        QnxService.loadCProject();

        Configuration configuration = QnxService.getBuildConfiguration( target );
        if ( configuration == null ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate target: " + target );
            throw new IllegalStateException();
        }

        String qnxDirName = QnxService.getQnxDirName();
        if ( qnxDirName == null ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate the `qnx*` direectory under: " + QnxService.getQnxTargetDirPath() );
            throw new IllegalStateException();
        }
    }
}
