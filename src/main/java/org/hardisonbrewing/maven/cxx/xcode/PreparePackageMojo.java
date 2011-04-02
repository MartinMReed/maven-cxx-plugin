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

import generated.Plist;

import java.io.File;

import org.hardisonbrewing.maven.core.ArchiveService;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-prepare-package
 * @phase prepare-package
 */
public final class PreparePackageMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() {

        if ( target != null ) {
            execute( target );
        }
        else {
            for (String target : XCodeService.getTargets()) {
                execute( target );
            }
        }
    }

    private void execute( String target ) {

        copyConfigBuildFiles( target );

        if ( XCodeService.isApplicationType( target ) ) {

            copyProvisioningFile( target );

            try {
                copyIpaFile( target );
            }
            catch (Exception e) {
                getLog().error( "Unable to create IPA file: " + target, e );
                throw new IllegalStateException( e );
            }
        }

        copyIconFile( target );
    }

    private void copyIconFile( String target ) {

        Plist plist = XCodeService.readInfoPlist( target );
        if ( plist == null ) {
            return;
        }

        String bundleIconFileId = InfoPlistService.getString( plist, "CFBundleIconFile" );
        if ( bundleIconFileId == null || bundleIconFileId.length() == 0 ) {
            return;
        }

        File bundleIconFile = XCodeService.getProjectFile( bundleIconFileId );
        prepareTargetFile( target, bundleIconFile, bundleIconFile.getName() );
    }

    private String getConfigBuildDirPath( String target ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
        stringBuffer.append( File.separator );
        stringBuffer.append( XCodeService.getConfiguration( target ) );
        return stringBuffer.toString();
    }

    private void copyProvisioningFile( String target ) {

        StringBuffer srcFilePath = new StringBuffer();
        srcFilePath.append( getConfigBuildDirPath( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( PropertiesService.getTargetProductName( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( "embedded.mobileprovision" );
        File srcFile = new File( srcFilePath.toString() );

        prepareTargetFile( target, srcFile, target + ".mobileprovision" );
    }

    private void copyConfigBuildFiles( String target ) {

        String directoryRoot = getConfigBuildDirPath( target );
        File configBuildDir = new File( directoryRoot );

        getLog().info( "Copying files from: " + configBuildDir );

        for (File file : FileUtils.listFilesRecursive( configBuildDir )) {
            prepareTargetFile( target, file, getFilename( directoryRoot, file ) );
        }
    }

    private String getFilename( String directoryRoot, File file ) {

        return file.getAbsolutePath().substring( directoryRoot.length() );
    }

    private void copyIpaFile( String target ) throws Exception {

        String productReference = PropertiesService.getTargetProductName( target );

        String directoryRoot = getConfigBuildDirPath( target );

        StringBuffer appFilePath = new StringBuffer();
        appFilePath.append( directoryRoot );
        appFilePath.append( File.separator );
        appFilePath.append( productReference );
        File appFile = new File( appFilePath.toString() );

        StringBuffer payloadTempDirPath = new StringBuffer();
        payloadTempDirPath.append( TargetDirectoryService.getTargetBuildDirPath( target ) );
        payloadTempDirPath.append( File.separator );
        payloadTempDirPath.append( "IPA" );
        File payloadTempDir = new File( payloadTempDirPath.toString() );
        payloadTempDir.mkdirs();

        StringBuffer payloadDirPath = new StringBuffer();
        payloadDirPath.append( payloadTempDirPath );
        payloadDirPath.append( File.separator );
        payloadDirPath.append( "Payload" );
        payloadDirPath.append( File.separator );
        payloadDirPath.append( productReference );
        File payloadDir = new File( payloadDirPath.toString() );
        payloadDir.mkdirs();

        FileUtils.copyDirectory( appFile, payloadDir );

        StringBuffer destFilePath = new StringBuffer();
        destFilePath.append( payloadTempDirPath );
        destFilePath.append( File.separator );
        destFilePath.append( target );

        StringBuffer zipFilePath = new StringBuffer();
        zipFilePath.append( destFilePath );
        zipFilePath.append( ".zip" );
        File zipFile = new File( zipFilePath.toString() );

        ArchiveService.archive( payloadTempDir, zipFile );

        StringBuffer ipaFilePath = new StringBuffer();
        ipaFilePath.append( destFilePath );
        ipaFilePath.append( ".ipa" );
        File ipaFile = new File( ipaFilePath.toString() );

        FileUtils.rename( zipFile, ipaFile );

        prepareTargetFile( target, ipaFile, getFilename( payloadTempDirPath.toString(), ipaFile ) );
    }

    private final void prepareTargetFile( String target, File src, String fileName ) {

        if ( XCodeService.getTargets().length > 1 ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( target );
            stringBuffer.append( File.separator );
            stringBuffer.append( fileName );
            fileName = stringBuffer.toString();
        }

        org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( src, fileName );
    }
}
