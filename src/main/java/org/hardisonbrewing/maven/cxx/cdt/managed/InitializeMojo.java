/**
 * Copyright (c) 2011-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.cdt.managed;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.model.Source;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;
import org.hardisonbrewing.maven.cxx.component.BuildConfiguration;

/**
 * @goal cdt-managed-initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        Configuration configuration = CProjectService.getBuildConfiguration( target );
        ToolChain toolChain = CdtService.getToolChain( configuration );
        Builder builder = toolChain.getBuilder();

        if ( builder.isMakefile() ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        List<String> includes = new ArrayList<String>();
        List<String> excludes = new ArrayList<String>();
        findExtensions( includes, excludes );
        loadSources( configuration, includes, excludes );
    }

    private void findExtensions( List<String> includes, List<String> excludes ) {

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
    }

    private final void loadSources( Configuration configuration, List<String> includes, List<String> excludes ) {

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

            Source source = new Source();
            source.directory = sourcePath;

            source.includes = new String[includes.size()];
            includes.toArray( source.includes );

            source.excludes = new String[excludes.size()];
            excludes.toArray( source.excludes );

            ProjectService.addSourceDirectory( source );
        }
    }

    private final BuildConfiguration getBuildConfiguration() {

        try {
            return lookup( BuildConfiguration.class );
        }
        catch (Exception e) {
            getLog().debug( "No custom build configuration found... skipping" );
            return null;
        }
    }
}
