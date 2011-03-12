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
import java.io.IOException;

import org.apache.maven.project.MavenProject;
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

    @Override
    public final void execute() {

        //http://developer.apple.com/library/ios/#featuredarticles/FA_Wireless_Enterprise_App_Distribution/Introduction/Introduction.html

        MavenProject project = getProject();
        String artifactId = project.getArtifactId();

        StringBuffer ipaManifestPath = new StringBuffer();
        ipaManifestPath.append( TargetDirectoryService.getTempPackagePath() );
        ipaManifestPath.append( ".ipa.xml" );
        getLog().info( "Generating " + ipaManifestPath + "..." );

        Template template = TemplateService.getTemplate( "/xcode/ipaManifest.vm" );

        String version = project.getVersion();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "groupId", project.getGroupId() );
        velocityContext.put( "version", version );
        velocityContext.put( "artifactId", artifactId );
        velocityContext.put( "description", project.getDescription() );

        File ipaManifestFile = new File( ipaManifestPath.toString() );

        try {
            TemplateService.writeTemplate( template, velocityContext, ipaManifestFile );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }
}
