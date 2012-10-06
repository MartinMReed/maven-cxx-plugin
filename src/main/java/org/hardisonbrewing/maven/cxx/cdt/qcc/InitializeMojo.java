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
package org.hardisonbrewing.maven.cxx.cdt.qcc;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.PropertiesService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;

/**
 * @goal cdt-qcc-initialize
 * @phase initialize
 */
public final class InitializeMojo extends org.hardisonbrewing.maven.cxx.cdt.InitializeMojo {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        Configuration configuration = CProjectService.getBuildConfiguration( target );

        if ( !QccToolChain.matches( configuration ) ) {
            getLog().info( "Not a QCC project... skipping" );
            return;
        }

        super.execute();

        QccToolChain toolChain = (QccToolChain) CdtService.getToolChain( configuration );
        QccToolChain.Options options = toolChain.getOptions();
        PropertiesService.putProperty( "CPUVARDIR", options.getPlatform() );
    }
}
