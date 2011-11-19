/**
 * Copyright (c) 2010-2011 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.xcode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-generate-ipa
 * @phase xcode-generate-ipa
 */
public final class GenerateIpaMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    @Override
    public void execute() {

        for (String target : XCodeService.getTargets()) {
            execute( target );
        }
    }

    private void execute( String target ) {

        if ( !XCodeService.isApplicationType( target ) ) {
            return;
        }
        generateIpaFile( target );
    }

    private void generateIpaFile( String target ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "xcrun" );
        cmd.add( "-sdk" );
        cmd.add( "iphoneos" );
        cmd.add( "PackageApplication" );

        StringBuffer appFilePath = new StringBuffer();
        appFilePath.append( TargetDirectoryService.getConfigBuildDirPath( target ) );
        appFilePath.append( File.separator );
        appFilePath.append( PropertiesService.getTargetProductName( target ) );
        cmd.add( appFilePath.toString() );

        StringBuffer ipaFilePath = new StringBuffer();
        ipaFilePath.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
        ipaFilePath.append( File.separator );
        ipaFilePath.append( target );
        ipaFilePath.append( "." );
        ipaFilePath.append( XCodeService.IPA_EXTENSION );

        cmd.add( "-o" );
        cmd.add( ipaFilePath.toString() );

        String codesignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codesignIdentity != null ) {
            cmd.add( "--sign" );
            cmd.add( codesignIdentity );
        }

        if ( provisioningProfile != null ) {
            File provisioningFile = InstallProvisioningProfileMojo.getProvisioningProfile( provisioningProfile );
            cmd.add( "--embed" );
            cmd.add( provisioningFile.getAbsolutePath() );
        }

        Commandline commandLine = buildCommandline( cmd );
        commandLine.addEnvironment( "CODESIGN_ALLOCATE", "/Developer/Platforms/iPhoneOS.platform/Developer/usr/bin/codesign_allocate" );

        execute( commandLine );
    }
}
