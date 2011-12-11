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

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal qde-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() {

        File cproject = QdeService.getCProjectFile();
        if ( !cproject.exists() ) {
            getLog().error( "Unable to locate .cproject file: " + cproject );
            throw new IllegalStateException();
        }

        CProjectService.loadCProject();

        Configuration targetConfiguration = CProjectService.getBuildConfiguration( target );
        if ( targetConfiguration == null ) {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "Unable to locate target `" );
            stringBuffer.append( target );
            stringBuffer.append( "`. " );

            Configuration[] configurations = CProjectService.getBuildConfigurations();
            if ( configurations == null ) {
                stringBuffer.append( "No targets are available!" );
            }
            else {

                stringBuffer.append( "Available targets are: " );

                for (int i = 0; i < configurations.length; i++) {

                    Configuration configuration = configurations[i];

                    if ( i > 0 ) {
                        stringBuffer.append( ", " );
                    }

                    stringBuffer.append( "`" );
                    stringBuffer.append( configuration.getName() );
                    stringBuffer.append( "`" );
                }

                stringBuffer.append( "." );
            }

            getLog().error( stringBuffer.toString() );
            throw new IllegalStateException();
        }
    }
}
