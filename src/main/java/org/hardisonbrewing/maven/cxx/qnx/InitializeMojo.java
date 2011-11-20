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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.component.BuildConfiguration;
import org.hardisonbrewing.maven.cxx.generic.Sources;

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

        loadSources();

        Configuration configuration = CProjectService.getBuildConfiguration( target );
        ToolChain toolChain = CProjectService.getToolChain( target );

        PropertiesService.putProperty( PropertiesService.QNX_TARGET, QnxService.getQnxTargetDirPath() );
        PropertiesService.putProperty( "CPUVARDIR", CProjectService.getPlatform( toolChain ) );

        loadSourcePaths( configuration );
    }

    private void loadSources() {

        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();

        loadSources( ProjectService.getSources(), includes, excludes );

        String[] sourceExtensions = CdtService.getSourceExtensions();
        if ( sourceExtensions != null ) {
            for (String sourceExtension : sourceExtensions) {
                String include = "**/*." + sourceExtension;
                if ( !includes.contains( include ) ) {
                    includes.add( include );
                }
            }
        }

        String[] resourceExtensions = CdtService.getResourceExtensions();
        if ( resourceExtensions != null ) {
            for (String resourceExtension : resourceExtensions) {
                String exclude = "**/*." + resourceExtension;
                if ( !excludes.contains( exclude ) ) {
                    excludes.add( exclude );
                }
            }
        }

        Sources sources = new Sources();

        if ( !includes.isEmpty() ) {
            String[] _includes = new String[includes.size()];
            includes.toArray( _includes );
            sources.setIncludes( _includes );
        }

        if ( !excludes.isEmpty() ) {
            String[] _excludes = new String[excludes.size()];
            excludes.toArray( _excludes );
            sources.setExcludes( _excludes );
        }

        ProjectService.setSources( sources );
    }

    private void loadSources( Sources sources, List<String> includes, List<String> excludes ) {

        if ( sources == null ) {
            return;
        }

        String[] _includes = sources.getIncludes();
        if ( _includes != null ) {
            for (String _include : _includes) {
                if ( !includes.contains( _include ) ) {
                    includes.add( _include );
                }
            }
        }

        String[] _excludes = sources.getExcludes();
        if ( _excludes != null ) {
            for (String _exclude : _excludes) {
                if ( !excludes.contains( _exclude ) ) {
                    excludes.add( _exclude );
                }
            }
        }
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
