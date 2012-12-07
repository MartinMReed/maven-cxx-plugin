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
package org.hardisonbrewing.maven.cxx.cdt.qcc.managed;

import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.cdt.CProjectService;
import org.hardisonbrewing.maven.cxx.cdt.CdtService;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain.Builder;
import org.hardisonbrewing.maven.cxx.qnx.PropertiesService;
import org.hardisonbrewing.maven.cxx.qnx.QnxService;

/**
 * @goal cdt-qcc-managed-validate
 * @phase validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

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

        QccToolChain toolChain = CdtService.getToolChain( configuration );
        Builder builder = toolChain.getBuilder();

        if ( builder.isMakefile() ) {
            getLog().info( "Not a managed project... skipping" );
            return;
        }

        if ( isBlackBerryNdkHomeRequired() ) {
            org.hardisonbrewing.maven.cxx.generic.ValidateMojo.checkPropertyExists( PropertiesService.BLACKBERRY_NDK_HOME, true );
        }

        String qnxHostUsrDirPath = QnxService.getQnxHostUsrDirPath();
        if ( qnxHostUsrDirPath == null ) {
            getLog().error( "Unable to locate `" + QnxService.QNX_USR_SEARCH + "` under <blackberry.ndk.home>" );
            throw new IllegalStateException();
        }
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
}
