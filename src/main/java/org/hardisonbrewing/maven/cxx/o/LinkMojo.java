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

package org.hardisonbrewing.maven.cxx.o;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.hardisonbrewing.maven.core.DependencyService;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.SourceFiles;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal o-link
 * @phase compile
 */
public final class LinkMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String language;

    /**
     * @parameter
     */
    public String[] libs;

    /**
     * @parameter
     */
    public String[] frameworks;

    @Override
    public void execute() {

        String[] sources = TargetDirectoryService.getProcessableSourceFilePaths();

        if ( sources == null ) {
            getLog().info( "No sources found... skipping linker" );
            return;
        }

        List<String> cmd = new LinkedList<String>();
        cmd.add( "c++".equals( language ) ? "g++" : "gcc" );

        cmd.add( "-o" );
        cmd.add( getProject().getArtifactId() + ".o" );

        for (String source : sources) {
            cmd.add( SourceFiles.replaceExtension( source, "o" ) );
        }

        try {
            buildArguments( cmd, getProject(), TargetDirectoryService.getProcessedSourcesDirectory() );
        }
        catch (Exception e) {
            throw new IllegalStateException( e.getMessage(), e );
        }

        if ( libs != null ) {
            for (String lib : libs) {
                cmd.add( "-l" + lib );
            }
        }

        if ( frameworks != null ) {
            for (String framework : frameworks) {
                cmd.add( "-framework" );
                cmd.add( framework );
            }
        }

        execute( cmd );
    }

    private final void buildArguments( List<String> cmd, MavenProject mavenProject, File dest ) throws Exception {

        for (Dependency dependency : (List<Dependency>) mavenProject.getDependencies()) {
            buildArguments( cmd, dependency, dest );
        }
    }

    private final void buildArguments( List<String> cmd, Dependency dependency, File dest ) throws Exception {

        StringBuffer destPath = new StringBuffer();
        destPath.append( dest );
        destPath.append( File.separator );
        destPath.append( dependency.getArtifactId() );
        dest = new File( destPath.toString() );

        appendLink( cmd, dependency, dest );

        Artifact artifact = DependencyService.createResolvedArtifact( dependency );
        buildArguments( cmd, ProjectService.getProject( artifact ), dest );
    }

    private final void appendLink( List<String> cmd, Dependency dependency, File dest ) throws Exception {

        if ( !"a".equals( dependency.getClassifier() ) ) {
            return;
        }

        StringBuffer archivePath = new StringBuffer();
        archivePath.append( dest );
        archivePath.append( File.separator );
        archivePath.append( dependency.getArtifactId() );
        archivePath.append( "." );
        archivePath.append( dependency.getClassifier() );
        File archive = new File( archivePath.toString() );

        if ( !archive.exists() ) {
            return;
        }

        cmd.add( "-L" + dest );
        cmd.add( "-l" + dependency.getArtifactId().substring( "lib".length() ) );
    }
}
