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

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;

/**
 * @goal cdt-managed-clean
 * @phase clean
 */
public final class CleanMojo extends JoJoMojoImpl {

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        Configuration[] configurations = CProjectService.getBuildConfigurations();
        for (Configuration configuration : configurations) {
            cleanBuilder( configuration );
        }
    }

    private void cleanBuilder( Configuration configuration ) {

        ToolChain toolChain = CdtService.getToolChain( configuration );
        Builder builder = toolChain.getBuilder();

        if ( builder.isMakefile() ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        String buildPath = builder.getBuildPath();

        if ( getLog().isDebugEnabled() ) {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "Cleaning builder name[" );
            stringBuffer.append( builder.getName() );
            stringBuffer.append( "], path[" );
            stringBuffer.append( builder.getBuildPath() );
            stringBuffer.append( "] -> path[" );
            stringBuffer.append( buildPath );
            stringBuffer.append( "]" );
            getLog().debug( stringBuffer.toString() );

        }

        File buildDir = new File( buildPath );

        if ( !buildDir.exists() ) {
            return;
        }

        try {
            FileUtils.deleteDirectory( buildDir );
        }
        catch (IOException e) {
            throw new IllegalStateException( e );
        }
    }
}
