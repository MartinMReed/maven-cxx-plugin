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
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal xcode-ipa-manifest
 * @phase xcode-ipa-manifest
 */
public final class GenerateIpaManifestMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String ipaServerBaseUrl;

    @Override
    public final void execute() {

        if ( !XCodeService.isApplicationType() ) {
            getLog().info( "No targets found of type 'com.apple.product-type.application`...skipping" );
            return;
        }

        //http://developer.apple.com/library/ios/#featuredarticles/FA_Wireless_Enterprise_App_Distribution/Introduction/Introduction.html

        if ( ipaServerBaseUrl != null ) {
            VelocityContext xmlContext = new VelocityContext();
            xmlContext.put( "serverBaseUrl", ipaServerBaseUrl );
            generate( "plist", xmlContext );
        }

        VelocityContext vmContext = new VelocityContext();
        vmContext.put( "serverBaseUrl", "${serverBaseUrl}" );
        generate( "vm", vmContext );
    }

    private final void generate( String extension, VelocityContext velocityContext ) {

        StringBuffer destPath = new StringBuffer();
        destPath.append( TargetDirectoryService.getTempPackagePath() );
        destPath.append( File.separator );
        destPath.append( "manifest." );
        destPath.append( extension );
        File dest = new File( destPath.toString() );

        if ( !dest.getParentFile().exists() ) {
            dest.getParentFile().mkdirs();
        }

        getLog().info( "Generating " + destPath + "..." );

        Plist plist = XCodeService.readInfoPlist();
        String bundleIconFile = InfoPlistService.getString( plist, "CFBundleIconFile" );
        String bundleName = InfoPlistService.getString( plist, "CFBundleName" );

        velocityContext.put( "ipaUrl", getProject().getArtifactId() + ".ipa" );
        velocityContext.put( "downloadIconUrl", bundleIconFile );
        velocityContext.put( "itunesIconUrl", bundleIconFile );
        velocityContext.put( "bundleIdentifier", XCodeService.getBundleIdentifier() );
        velocityContext.put( "bundleVersion", XCodeService.getBundleVersion() );
        velocityContext.put( "title", bundleName );

        Template template = TemplateService.getTemplate( "/xcode/ipaManifest.vm" );

        try {
            TemplateService.writeTemplate( template, velocityContext, dest );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
