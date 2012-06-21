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
package org.hardisonbrewing.maven.cxx.flex;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.cli.CommandLineService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;

/**
 * @goal flex-air-compile
 * @phase compile
 */
public class AirCompileMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    private String target;

    /**
     * @parameter
     */
    private KeyStore keystore;

    /**
     * @parameter
     */
    private String provisioningProfile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String targetExt;
        if ( FlexService.isIosTarget( target ) ) {
            targetExt = FlexService.IOS_TARGET_EXT;
        }
        else if ( FlexService.isAndroidTarget( target ) ) {
            targetExt = FlexService.ANDROID_TARGET_EXT;
        }
        else {
            targetExt = FlexService.AIR_TARGET_EXT;
        }

        String artifactId = getProject().getArtifactId();
        getLog().info( "Building " + artifactId + "." + targetExt + "..." );

        // ADT packaging: http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html
        List<String> cmd = new LinkedList<String>();
        cmd.add( "adt" );

        cmd.add( "-package" );

        boolean iosTarget = FlexService.isIosTarget( target );
        boolean androidTarget = FlexService.isAndroidTarget( target );

        // signing comes before target for non-mobile
        // http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html
        if ( !iosTarget && !androidTarget ) {
            addKeyStoreSigning( cmd );
        }

        cmd.add( "-target" );
        cmd.add( target );

        // signing comes after target for mobile
        // http://help.adobe.com/en_US/air/build/WS901d38e593cd1bac1e63e3d128cdca935b-8000.html
        if ( iosTarget || androidTarget ) {
            addKeyStoreSigning( cmd );
        }

        if ( iosTarget ) {
            addProvisioning( cmd );
        }

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        StringBuffer airFilePath = new StringBuffer();
        airFilePath.append( targetDirectoryPath );
        airFilePath.append( File.separator );
        airFilePath.append( artifactId );
        airFilePath.append( "." );
        airFilePath.append( targetExt );
        cmd.add( airFilePath.toString() );

        StringBuffer airiFilePath = new StringBuffer();
        airiFilePath.append( targetDirectoryPath );
        airiFilePath.append( File.separator );
        airiFilePath.append( artifactId );
        airiFilePath.append( ".airi" );
        cmd.add( airiFilePath.toString() );

        String sdkHome = PropertiesService.getProperty( PropertiesService.ADOBE_FLEX_HOME );

        Commandline commandLine = buildCommandline( cmd );
        CommandLineService.appendEnvVar( commandLine, "PATH", sdkHome + File.separator + "bin" );
        execute( commandLine );
    }

    private void addProvisioning( List<String> cmd ) {

        String provisioningProfilePath = provisioningProfile;
        if ( !provisioningProfilePath.startsWith( File.separator ) ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( provisioningProfile );
            provisioningProfilePath = stringBuffer.toString();
        }
        cmd.add( "-provisioning-profile" );
        cmd.add( provisioningProfilePath );
    }

    private void addKeyStoreSigning( List<String> cmd ) {

        // ADT signing: http://help.adobe.com/en_US/air/build/WS5b3ccc516d4fbf351e63e3d118666ade46-7f72.html
        cmd.add( "-storetype" );
        cmd.add( "pkcs12" );

        String keystorePath = keystore.keystore;
        if ( !keystorePath.startsWith( File.separator ) ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( keystore.keystore );
            keystorePath = stringBuffer.toString();
        }
        cmd.add( "-keystore" );
        cmd.add( keystorePath );

        cmd.add( "-storepass" );
        cmd.add( keystore.storepass );

        if ( keystore.alias != null && keystore.alias.length() > 0 ) {
            cmd.add( "-alias" );
            cmd.add( keystore.alias );
        }

        if ( keystore.keypass != null && keystore.keypass.length() > 0 ) {
            cmd.add( "-keypass" );
            cmd.add( keystore.keypass );
        }
    }
}
