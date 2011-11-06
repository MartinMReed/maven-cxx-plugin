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

import generated.org.eclipse.cdt.Cproject;
import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;

/**
 * @goal o-qnx-compile
 * @phase compile
 */
public class CompileMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File file = new File( ProjectService.getBaseDirPath(), ".cproject" );
        Cproject cproject = CProjectService.readCProject( file );

        String configurationName = "Device-Release";
        Configuration configuration = CProjectService.getBuildConfiguration( cproject, CProjectService.MODULE_SETTINGS, configurationName );
        getLog().info( configuration.getOtherAttributes().get( QName.valueOf( CProjectService.CONFIG_BUILD_PROPERTIES ) ) );
    }
}
