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
package org.hardisonbrewing.maven.cxx.swf;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal swf-validate
 * @phase validate
 */
public class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String descriptorFile;

    /**
     * @parameter
     */
    private String sourceFile;

    /**
     * @parameter
     */
    private KeyStore keystore;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.ADOBE_FLEX_HOME, true );

        if ( descriptorFile == null || descriptorFile.length() == 0 ) {
            getLog().error( "<descriptorFile/> must be set!" );
            throw new IllegalArgumentException();
        }

        if ( sourceFile == null || sourceFile.length() == 0 ) {
            getLog().error( "<sourceFile/> must be set!" );
            throw new IllegalArgumentException();
        }

        if ( keystore == null ) {
            getLog().error( "<keystore/> must be set!" );
            throw new IllegalArgumentException();
        }

        if ( keystore.keystore == null ) {
            getLog().error( "<keystore><keystore/></keystore> must be set!" );
            throw new IllegalArgumentException();
        }

        if ( keystore.storepass == null ) {
            getLog().error( "<keystore><storepass/></keystore> must be set!" );
            throw new IllegalArgumentException();
        }
    }
}
