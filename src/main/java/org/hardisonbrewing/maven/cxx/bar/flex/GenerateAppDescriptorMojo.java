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
package org.hardisonbrewing.maven.cxx.bar.flex;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TemplateService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal bar-generate-app-descriptor
 * @phase generate-sources
 */
public class GenerateAppDescriptorMojo extends JoJoMojoImpl {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        MavenProject project = getProject();
        String artifactId = project.getArtifactId();

        if ( !shouldExecute() ) {
            getLog().info( artifactId + ".xml is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Generating " + artifactId + ".xml..." );

        Template template = TemplateService.getTemplateFromClasspath( "/bar/appDescriptor.vm" );

        String version = project.getVersion();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "groupId", project.getGroupId() );
        velocityContext.put( "version", getResolvedVersion( version ) );
        velocityContext.put( "versionLabel", version );
        velocityContext.put( "artifactId", artifactId );
        velocityContext.put( "description", project.getDescription() );

        StringBuffer appDescriptorPath = new StringBuffer();
        appDescriptorPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        appDescriptorPath.append( File.separator );
        appDescriptorPath.append( artifactId );
        appDescriptorPath.append( ".xml" );

        File appDescriptorFile = new File( appDescriptorPath.toString() );

        try {
            TemplateService.writeTemplate( template, velocityContext, appDescriptorFile );
        }
        catch (IOException e) {
            throw new IllegalStateException( e.getMessage(), e );
        }
    }

    private final String getResolvedVersion( String version ) {

        if ( !version.endsWith( "-SNAPSHOT" ) ) {
            return version;
        }
        else {
            // versions can only be <0-999>.<0-999>.<0-999>
            int indexOf = version.indexOf( "-SNAPSHOT" );
            return version.substring( 0, indexOf );
        }
    }

    private final boolean shouldExecute() {

        String artifactId = getProject().getArtifactId();

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( artifactId );
        outputPath.append( ".xml" );

        File outputFile = new File( outputPath.toString() );
        if ( outputFile.exists() ) {
            if ( outputFile.lastModified() >= getLatestFileDate() ) {
                return false;
            }
        }

        return true;
    }

    private final long getLatestFileDate() {

        StringBuffer sourcePath = new StringBuffer();
        sourcePath.append( getProject().getBasedir() );
        sourcePath.append( "pom.xml" );

        File sourceFile = new File( sourcePath.toString() );
        return sourceFile.lastModified();
    }
}
