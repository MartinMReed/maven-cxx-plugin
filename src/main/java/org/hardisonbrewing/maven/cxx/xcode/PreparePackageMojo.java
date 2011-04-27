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
            copyIpaFile( target );
        }

        copyIconFile( target );
    }

    private void copyIconFile( String target ) {

        Plist plist = XCodeService.readInfoPlist( XCodeService.getConvertedInfoPlist( target ) );
        if ( plist == null ) {
            return;
        }

        String bundleIconFileId = InfoPlistService.getString( plist, "CFBundleIconFile" );
        if ( bundleIconFileId == null || bundleIconFileId.length() == 0 ) {
            getLog().warn( "There was no CFBundleIconFile specified in the Info.plist." );
            return;
        }

        File bundleIconFile = XCodeService.getProjectFile( bundleIconFileId );
        if ( bundleIconFile == null ) {
            getLog().error( "CFBundleIconFile was specified in the Info.plist but could not be located: " + bundleIconFileId );
            throw new IllegalStateException();
        }

        prepareTargetFile( target, bundleIconFile, bundleIconFile.getName() );
    }

    private void copyProvisioningFile( String target ) {

        StringBuffer srcFilePath = new StringBuffer();
        srcFilePath.append( TargetDirectoryService.getConfigBuildDirPath( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( PropertiesService.getTargetProductName( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( "embedded.mobileprovision" );
        File srcFile = new File( srcFilePath.toString() );

        prepareTargetFile( target, srcFile, target + ".mobileprovision" );
    }

    private void copyConfigBuildFiles( String target ) {

        String directoryRoot = TargetDirectoryService.getConfigBuildDirPath( target );
        File configBuildDir = new File( directoryRoot );

        getLog().info( "Copying files from: " + configBuildDir );

        for (File file : FileUtils.listFilesRecursive( configBuildDir )) {
            prepareTargetFile( target, file, getFilename( directoryRoot, file ) );
        }
    }

    private String getFilename( String directoryRoot, File file ) {

        return file.getAbsolutePath().substring( directoryRoot.length() );
    }

    private void copyIpaFile( String target ) {

        String rootDirectory = TargetDirectoryService.getTargetBuildDirPath( target );

        StringBuffer ipaFilePath = new StringBuffer();
        ipaFilePath.append( rootDirectory );
        ipaFilePath.append( File.separator );
        ipaFilePath.append( target );
        ipaFilePath.append( ".ipa" );
        File ipaFile = new File( ipaFilePath.toString() );

        prepareTargetFile( target, ipaFile, getFilename( rootDirectory, ipaFile ) );
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
