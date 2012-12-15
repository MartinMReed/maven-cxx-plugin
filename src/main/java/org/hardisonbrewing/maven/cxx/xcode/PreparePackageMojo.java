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

import generated.plist.Plist;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String[] targets = XCodeService.getTargets();

        if ( scheme != null ) {

            for (String target : targets) {

                copyProductFile( target );

                if ( XCodeService.isApplicationType( target ) ) {
                    copyProvisioningFile( target );
                    copyIpaFile( target );
                }

                copyIconFile( target );
            }

            String[] excludes = new String[targets.length];
            for (int i = 0; i < excludes.length; i++) {
                excludes[i] = getProductFileInclude( targets[i] );
            }

            copyConfigBuildFiles( scheme, excludes );
        }
        else {

            for (String target : targets) {

                copyConfigBuildFiles( target, null );

                if ( XCodeService.isApplicationType( target ) ) {
                    copyProvisioningFile( target );
                    copyIpaFile( target );
                }

                copyIconFile( target );
            }
        }
    }

    private void copyIconFile( String target ) {

        String plistPath = XCodeService.getConvertedInfoPlistPath( target );
        Plist plist = XCodeService.readInfoPlist( new File( plistPath ) );

        if ( plist == null ) {
            return;
        }

        String bundleIconFileId = InfoPlistService.getString( plist, InfoPlistService.PROP_BUNDLE_ICON );
        if ( bundleIconFileId == null || bundleIconFileId.length() == 0 ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "There was no `" );
            stringBuffer.append( InfoPlistService.PROP_BUNDLE_ICON );
            stringBuffer.append( "` specified in the " );
            stringBuffer.append( InfoPlistService.INFO_PLIST );
            stringBuffer.append( "." );
            getLog().warn( stringBuffer.toString() );
            return;
        }

        File bundleIconFile = XCodeService.getProjectFile( bundleIconFileId );
        if ( bundleIconFile == null ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( "`" );
            stringBuffer.append( InfoPlistService.PROP_BUNDLE_ICON );
            stringBuffer.append( "` was specified in the " );
            stringBuffer.append( InfoPlistService.INFO_PLIST );
            stringBuffer.append( " but could not be located: " );
            stringBuffer.append( bundleIconFileId );
            getLog().error( stringBuffer.toString() );
            throw new IllegalStateException();
        }

        prepareTargetFile( target, bundleIconFile, bundleIconFile.getName() );
    }

    private void copyProductFile( String target ) {

        String directoryRoot = TargetDirectoryService.getConfigBuildDirPath( target );
        File configBuildDir = new File( directoryRoot );

        String productName = PropertiesService.getTargetProductName( target );
        File productFile = new File( configBuildDir, productName );

        if ( !productFile.isDirectory() ) {
            prepareTargetFile( target, productFile, getFilename( directoryRoot, productFile ) );
        }

        getLog().info( "Copying product files for target[" + target + "] from: " + configBuildDir );

        String[] includes = new String[] { getProductFileInclude( target ) };

        for (File file : FileUtils.listFilesRecursive( configBuildDir, includes, null )) {
            prepareTargetFile( target, file, getFilename( directoryRoot, file ) );
        }
    }

    private String getProductFileInclude( String target ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PropertiesService.getTargetProductName( target ) );
        stringBuffer.append( "*" );
        stringBuffer.append( File.separator );
        stringBuffer.append( "**" );
        return stringBuffer.toString();
    }

    private void copyProvisioningFile( String target ) {

        StringBuffer srcFilePath = new StringBuffer();
        srcFilePath.append( TargetDirectoryService.getConfigBuildDirPath( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( PropertiesService.getTargetProductName( target ) );
        srcFilePath.append( File.separator );
        srcFilePath.append( "embedded." );
        srcFilePath.append( XCodeService.MOBILEPROVISION_EXTENSION );
        File srcFile = new File( srcFilePath.toString() );

        StringBuffer destFilePath = new StringBuffer();
        destFilePath.append( target );
        destFilePath.append( "." );
        destFilePath.append( XCodeService.MOBILEPROVISION_EXTENSION );

        prepareTargetFile( target, srcFile, destFilePath.toString() );
    }

    private void copyConfigBuildFiles( String target, String[] excludes ) {

        String directoryRoot = TargetDirectoryService.getConfigBuildDirPath( target );
        File configBuildDir = new File( directoryRoot );

        getLog().info( "Copying files from: " + configBuildDir );

        for (File file : FileUtils.listFilesRecursive( configBuildDir, null, excludes )) {
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
        ipaFilePath.append( "." );
        ipaFilePath.append( XCodeService.IPA_EXTENSION );
        File ipaFile = new File( ipaFilePath.toString() );

        prepareTargetFile( target, ipaFile, getFilename( rootDirectory, ipaFile ) );
    }

    private final void prepareTargetFile( String target, File src, String filename ) {

        String[] targets = XCodeService.getTargets();

        if ( targets.length > 1 ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( target );
            stringBuffer.append( File.separator );
            stringBuffer.append( filename );
            filename = stringBuffer.toString();
        }

        org.hardisonbrewing.maven.cxx.generic.PreparePackageMojo.prepareTargetFile( src, filename );
    }
}
