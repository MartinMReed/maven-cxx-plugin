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
import generated.org.eclipse.cdt.ToolChain;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.component.BuildConfiguration;

/**
 * @goal qnx-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        CdtService.setEclipseDirPath( QnxService.getEclipseDirPath() );
        CdtService.loadCdtCoreFileExtensions();

        Configuration configuration = QnxService.getBuildConfiguration( target );
        ToolChain toolChain = QnxService.getToolChain( target );

        PropertiesService.putProperty( PropertiesService.QNX_TARGET, QnxService.getQnxTargetDirPath() );
        PropertiesService.putProperty( "CPUVARDIR", QnxService.getPlatform( toolChain ) );

        loadSourcePaths( configuration );
    }

    private final void loadSourcePaths( Configuration configuration ) {

        String[] sourcePaths = CProjectService.getSourcePaths( configuration );
        if ( sourcePaths == null ) {
            return;
        }

        BuildConfiguration buildConfiguration = getBuildConfiguration();
        String sourceDirectory = getProject().getBuild().getSourceDirectory();
        if ( !sourceDirectory.equals( buildConfiguration.getSourceDirectory() ) ) {
            ProjectService.setSourceDirectory( sourcePaths[0] );
        }

        for (String sourcePath : sourcePaths) {
            ProjectService.addSourceDirectory( sourcePath );
        }
    }

    private final BuildConfiguration getBuildConfiguration() {

        try {
            return lookup( BuildConfiguration.class, getProject().getPackaging() );
        }
        catch (Exception e) {
            getLog().debug( "No custom build configuration found... skipping" );
            return null;
        }
    }
}
