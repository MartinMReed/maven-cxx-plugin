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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.ArchiveService;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal xcode-prepare-package
 * @phase prepare-package
 */
public final class PreparePackageMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        copyConfigBuildFiles();

        if ( XCodeService.isApplicationType() ) {

            try {
                copyIpaFile();
            }
            catch (Exception e) {
                getLog().error( "Unable to create IPA file", e );
                throw new IllegalStateException( e );
            }

            copyIconFile();
        }
    }

    private void copyIconFile() {

        Plist plist = XCodeService.readInfoPlist();
        String iconFilename = InfoPlistService.getString( plist, "CFBundleIconFile" );
        if ( iconFilename == null || iconFilename.length() == 0 ) {
            return;
        }

        String directoryRoot = ProjectService.getBaseDirPath();

        StringBuffer iconFilePath = new StringBuffer();
        iconFilePath.append( directoryRoot );
        iconFilePath.append( File.separator );
        iconFilePath.append( iconFilename );
        File iconFile = new File( iconFilePath.toString() );

        String filename = getFilename( directoryRoot, iconFile );
        org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( iconFile, filename );
    }

    private String getConfigBuildDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( XCodeService.getConfiguration() );
        return stringBuffer.toString();
    }

    private void copyConfigBuildFiles() {

        String directoryRoot = getConfigBuildDirPath();
        File configBuildDir = new File( directoryRoot );

        getLog().info( "Copying files from: " + configBuildDir );

        for (File file : FileUtils.listFilesRecursive( configBuildDir )) {
            String filename = getFilename( directoryRoot, file );
            org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( file, filename );
        }
    }

    private String getFilename( String directoryRoot, File file ) {

        return file.getAbsolutePath().substring( directoryRoot.length() );
    }

    private void copyIpaFile() throws Exception {

        String directoryRoot = getConfigBuildDirPath();

        StringBuffer appFilePath = new StringBuffer();
        appFilePath.append( directoryRoot );
        appFilePath.append( File.separator );
        appFilePath.append( getProject().getArtifactId() );
        appFilePath.append( ".app" );
        File appFile = new File( appFilePath.toString() );

        StringBuffer payloadTempDirPath = new StringBuffer();
        payloadTempDirPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        payloadTempDirPath.append( File.separator );
        payloadTempDirPath.append( "ipa_payload" );
        File payloadTempDir = new File( payloadTempDirPath.toString() );

        StringBuffer payloadDirPath = new StringBuffer();
        payloadDirPath.append( payloadTempDirPath );
        payloadDirPath.append( File.separator );
        payloadDirPath.append( "Payload" );
        payloadDirPath.append( File.separator );
        payloadDirPath.append( appFile.getName() );
        File payloadDir = new File( payloadDirPath.toString() );

        if ( !payloadDir.exists() ) {
            payloadDir.mkdirs();
        }

        FileUtils.copyDirectory( appFile, payloadDir );

        StringBuffer destFilePath = new StringBuffer();
        destFilePath.append( payloadTempDirPath );
        destFilePath.append( File.separator );
        destFilePath.append( getProject().getArtifactId() );

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

        String ipaFilename = getFilename( payloadTempDirPath.toString(), ipaFile );
        org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( ipaFile, ipaFilename );
    }
}
