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
import generated.org.eclipse.cdt.ToolChain.Builder;

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal qnx-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public final void execute() {

        if ( isBlackBerryNdkHomeRequired() ) {
            org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_NDK_HOME, true );
        }

        File cproject = QnxService.getCProjectFile();
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

        ToolChain targetToolChain = CProjectService.getToolChain( targetConfiguration );
        Builder builder = targetToolChain.getBuilder();
        getLog().info( "Using builder: " + builder.getName() );
        if ( CProjectService.isMakefileBuilder( builder ) ) {
            getLog().error( "Makefile builds are not currently supported!" );
            throw new UnsupportedOperationException();
        }

        String qnxHostUsrDirPath = QnxService.getQnxHostUsrDirPath();
        if ( qnxHostUsrDirPath == null ) {
            getLog().error( "Unable to locate `" + QnxService.QNX_USR_SEARCH + "` under <blackberry.ndk.home>" );
            throw new IllegalStateException();
        }

        File eclipseDir = new File( QnxService.getEclipseDirPath() );
        if ( !eclipseDir.exists() ) {
            getLog().error( "Unable to locate Eclipse directory: " + eclipseDir );
            throw new IllegalStateException();
        }
    }

    private boolean isEnvVarPathValid( String key, String propertyRequired ) {

        File file = PropertiesService.getPropertyAsFile( PropertiesService.envVarKey( key ) );
        if ( file == null || !file.exists() ) {

            if ( file == null ) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append( "Environment variable `" );
                stringBuffer.append( key );
                stringBuffer.append( "` is not set. `" );
                stringBuffer.append( propertyRequired );
                stringBuffer.append( "` will be required." );
                getLog().warn( stringBuffer.toString() );
            }
            else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append( "Environment variable `" );
                stringBuffer.append( key );
                stringBuffer.append( "` is not set.  is set but is not accessible. `" );
                stringBuffer.append( propertyRequired );
                stringBuffer.append( "` will be required." );
                getLog().warn( stringBuffer.toString() );
            }

            return false;
        }

        return true;
    }

    private boolean isBlackBerryNdkHomeRequired() {

        if ( !isEnvVarPathValid( PropertiesService.QNX_TARGET, PropertiesService.BLACKBERRY_NDK_HOME ) ) {
            return true;
        }

        if ( !isEnvVarPathValid( PropertiesService.QNX_HOST, PropertiesService.BLACKBERRY_NDK_HOME ) ) {
            return true;
        }

        File qnxTargetBaseDir = PropertiesService.getPropertyAsFile( PropertiesService.ENV_QNX_TARGET );

        while (qnxTargetBaseDir != null) {
            String filename = qnxTargetBaseDir.getName();
            qnxTargetBaseDir = qnxTargetBaseDir.getParentFile();
            if ( "target".equals( filename ) ) {
                break;
            }
        }

        if ( qnxTargetBaseDir == null || !qnxTargetBaseDir.exists() ) {

            if ( qnxTargetBaseDir == null ) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append( "Unable to extract `" );
                stringBuffer.append( PropertiesService.BLACKBERRY_NDK_HOME );
                stringBuffer.append( "` from environment variable `" );
                stringBuffer.append( PropertiesService.QNX_HOST );
                stringBuffer.append( "`. `" );
                stringBuffer.append( PropertiesService.BLACKBERRY_NDK_HOME );
                stringBuffer.append( "` will be required." );
                getLog().warn( stringBuffer.toString() );
            }
            else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append( "Extracted `" );
                stringBuffer.append( PropertiesService.BLACKBERRY_NDK_HOME );
                stringBuffer.append( "` from environment variable `" );
                stringBuffer.append( PropertiesService.QNX_HOST );
                stringBuffer.append( "`, but is not accessible. `" );
                stringBuffer.append( PropertiesService.BLACKBERRY_NDK_HOME );
                stringBuffer.append( "` will be required." );
                getLog().warn( stringBuffer.toString() );
            }

            return true;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( "Environment variables `" );
        stringBuffer.append( PropertiesService.QNX_TARGET );
        stringBuffer.append( "` and `" );
        stringBuffer.append( PropertiesService.QNX_HOST );
        stringBuffer.append( "` are set and will be used instead of `" );
        stringBuffer.append( PropertiesService.BLACKBERRY_NDK_HOME );
        stringBuffer.append( "`." );
        getLog().debug( stringBuffer.toString() );

        PropertiesService.putProperty( PropertiesService.BLACKBERRY_NDK_HOME, qnxTargetBaseDir.getPath() );

        return false;
    }
}
