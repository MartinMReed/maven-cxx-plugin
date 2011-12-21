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
package org.hardisonbrewing.maven.cxx.qde.managed;

import generated.org.eclipse.cdt.ToolChain.Builder;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.qde.CProjectService;

/**
 * @goal qde-managed-clean
 * @phase clean
 */
public final class CleanMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        Builder builder = CProjectService.getBuilder( target );

        if ( CProjectService.isMakefileBuilder( builder ) ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        File buildDir = new File( CProjectService.getBuildPath( builder ) );

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
