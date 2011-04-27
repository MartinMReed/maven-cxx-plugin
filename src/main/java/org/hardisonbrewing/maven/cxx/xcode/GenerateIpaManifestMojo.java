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
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TemplateService;

/**
 * @goal xcode-ipa-manifest
 * @phase xcode-ipa-manifest
 */
public final class GenerateIpaManifestMojo extends JoJoMojoImpl {

    @Override
    public final void execute() {

        for (String target : XCodeService.getTargets()) {
            execute( target );
        }
    }

    private final void execute( String target ) {

        if ( !XCodeService.isApplicationType( target ) ) {
            getLog().info( "No targets found of type 'com.apple.product-type.application`...skipping" );
            return;
        }

        // http://developer.apple.com/library/ios/#featuredarticles/FA_Wireless_Enterprise_App_Distribution/Introduction/Introduction.html

        StringBuffer destPath = new StringBuffer();
        destPath.append( TargetDirectoryService.getTempPackagePath( target ) );
        destPath.append( File.separator );
        destPath.append( "manifest.vm" );
        File dest = new File( destPath.toString() );

        if ( !dest.getParentFile().exists() ) {
            dest.getParentFile().mkdirs();
        }

        getLog().info( "Generating " + destPath + "..." );

        Plist plist = XCodeService.readInfoPlist( XCodeService.getConvertedInfoPlist( target ) );

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "ipaUrl", target + ".ipa" );
        velocityContext.put( "bundleIdentifier", XCodeService.getBundleIdentifier() );
        velocityContext.put( "bundleVersion", XCodeService.getBundleVersion() );
        velocityContext.put( "title", InfoPlistService.getString( plist, "CFBundleDisplayName" ) );

        File iconFile = null;

        String bundleIconFileId = InfoPlistService.getString( plist, "CFBundleIconFile" );
        if ( bundleIconFileId == null || bundleIconFileId.length() == 0 ) {
            getLog().warn( "There was no CFBundleIconFile specified in the Info.plist." );
            velocityContext.put( "downloadIconUrl", "" );
            velocityContext.put( "itunesIconUrl", "" );
        }
        else {
            iconFile = XCodeService.getProjectFile( bundleIconFileId );
            if ( iconFile == null ) {
                getLog().error( "CFBundleIconFile was specified in the Info.plist but could not be located: " + bundleIconFileId );
                throw new IllegalStateException();
            }
            velocityContext.put( "downloadIconUrl", "${serverBaseUrl}" + iconFile.getName() );
            velocityContext.put( "itunesIconUrl", "${serverBaseUrl}" + iconFile.getName() );
        }

        Template template = TemplateService.getTemplate( "/xcode/ipaManifest.vm" );

        try {
            TemplateService.writeTemplate( template, velocityContext, dest );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
