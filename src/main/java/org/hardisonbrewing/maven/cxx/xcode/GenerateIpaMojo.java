/**
 * Copyright (c) 2010-2012 Martin M Reed
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
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.cli.CommandLineService;

/**
 * @goal xcode-generate-ipa
 * @phase prepare-package
 */
public final class GenerateIpaMojo extends JoJoMojoImpl {

    private static final String CODESIGN_ALLOCATE = "CODESIGN_ALLOCATE";
    private static final String CODESIGN_ALLOCATE_XCODE_4_3 = "/Developer/Platforms/iPhoneOS.platform/Developer/usr/bin/codesign_allocate";
    private static final String CODESIGN_ALLOCATE_XCODE_4_5 = "/Applications/Xcode.app/Contents/Developer/usr/bin/codesign_allocate";

    /**
     * @parameter
     */
    public String provisioningProfile;

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if ( scheme != null ) {
            String target = XCodeService.getBuildTargetName( scheme );
            execute( target );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                execute( target );
            }
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

        cmd.add( XCodeService.getProductFilePath( target ) );

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

        Commandline commandLine = buildCommandline( cmd );
        if ( CommandLineService.getEnvVar( commandLine, CODESIGN_ALLOCATE ) == null ) {
            commandLine.addEnvironment( CODESIGN_ALLOCATE, getCodesignAllocate() );
        }
        execute( commandLine );
    }

    private String getCodesignAllocate() {

        String codesignAllocation = whichCodesignAllocate();
        if ( codesignAllocation != null && codesignAllocation.length() > 0 ) {
            return codesignAllocation;
        }

        if ( FileUtils.exists( CODESIGN_ALLOCATE_XCODE_4_5 ) ) {
            return CODESIGN_ALLOCATE_XCODE_4_5;
        }

        if ( FileUtils.exists( CODESIGN_ALLOCATE_XCODE_4_3 ) ) {
            return CODESIGN_ALLOCATE_XCODE_4_3;
        }

        getLog().error( "Unable to locate `codesign_allocate`. Try adding it to your env var PATH or set CODESIGN_ALLOCATE to point to it." );
        throw new IllegalStateException();
    }

    private String whichCodesignAllocate() {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "which" );
        cmd.add( "codesign_allocate" );

        StringStreamConsumer streamConsumer = new StringStreamConsumer();
        execute( cmd, streamConsumer, null );

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
