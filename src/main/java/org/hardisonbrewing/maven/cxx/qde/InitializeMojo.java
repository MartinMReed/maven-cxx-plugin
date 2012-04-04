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
package org.hardisonbrewing.maven.cxx.qde;

import generated.org.eclipse.cdt.StorageModule.Configuration;
import generated.org.eclipse.cdt.ToolChain;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;

/**
 * @goal qde-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        CdtService.setEclipseDirPath( QdeService.getEclipseDirPath() );
        CdtService.loadCdtCoreFileExtensions();

        PropertiesService.putProperty( PropertiesService.QNX_TARGET, QdeService.getQnxTargetDirPath() );

        ToolChain toolChain = CProjectService.getToolChain( target );
        PropertiesService.putProperty( "CPUVARDIR", CProjectService.getPlatform( toolChain ) );

        Configuration configuration = CProjectService.getBuildConfiguration( target );
        if ( CProjectService.isApplication( configuration ) ) {
            BarDescriptorService.loadBarDescriptor();
        }
    }
}
