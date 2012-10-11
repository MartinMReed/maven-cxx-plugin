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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-generate-ipa
 * @phase prepare-package
 */
public final class GenerateIpaMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

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

        cmd.add( getAppFilePath( target ) );

        cmd.add( "-o" );
        cmd.add( getIpaFilePath( target ) );

        String codesignIdentity = PropertiesService.getXCodeProperty( XCodeService.CODE_SIGN_IDENTITY );
        if ( codesignIdentity != null ) {
            cmd.add( "--sign" );
            cmd.add( codesignIdentity );
        }

        if ( provisioningProfile != null ) {
            File provisioningFile = ProvisioningProfileService.getProvisioningProfile( provisioningProfile );
            cmd.add( "--embed" );
            cmd.add( provisioningFile.getAbsolutePath() );
        }

		String codesignAllocateLocation = getCodesignAllocateVariable();
		if (codesignAllocateLocation == null || codesignAllocateLocation.length() == 0) {
		
			codesignAllocateLocation = "/Developer/Platforms/iPhoneOS.platform/Developer/usr/bin/codesign_allocate";
		}

        Commandline commandLine = buildCommandline( cmd );
        commandLine.addEnvironment( "CODESIGN_ALLOCATE", codesignAllocateLocation );
        execute( commandLine );
    }

    private String getAppFilePath( String target ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getConfigBuildDirPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( PropertiesService.getTargetProductName( target ) );
        return stringBuffer.toString();
    }

	/**
	 * Check for the CODESIGN_ALLOCATE variable in the bash profile.
	 * Mountain Lion's codesign allocate tool is in a different location.
	 * This assumes that the dev has already set it to the preferred location.
	 */
	private String getCodesignAllocateVariable() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "echo" );
        cmd.add( "$CODESIGN_ALLOCATE" );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, streamConsumer );

        return streamConsumer.getOutput().trim();
	}

    private String getIpaFilePath( String target ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( target );
        stringBuffer.append( "." );
        stringBuffer.append( XCodeService.IPA_EXTENSION );
        return stringBuffer.toString();
    }
}
