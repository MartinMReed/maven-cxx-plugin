/**
 * Copyright (c) 2010 Martin M Reed
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

package org.hardisonbrewing.maven.core;

import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public abstract class JoJoMojoImpl extends JoJoMojo implements Contextualizable {

    /**
     * The current {@link MavenProject} instance.
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject mavenProject;

    /**
     * @component role="org.apache.maven.project.MavenProjectBuilder"
     * @required
     * @readonly
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * The current {@link ArchiverManager} instance.
     * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
     * @required
     */
    private ArchiverManager archiverManager;

    /**
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component role="org.apache.maven.artifact.installer.ArtifactInstaller"
     * @required
     * @readonly
     */
    private ArtifactInstaller artifactInstaller;

    /**
     * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     */
    private List<ArtifactRepository> remoteRepositories;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    private PlexusContainer plexusContainer;

    /**
     * Return the current {@link MavenProject} instance.
     * @return
     */
    public final MavenProject getProject() {

        return mavenProject;
    }

    /**
     * Return the current {@link ArchiverManager} instance.
     * @return
     */
    public final ArchiverManager getArchiverManager() {

        return archiverManager;
    }

    /**
     * 
     * @return
     */
    public final ArtifactResolver getArtifactResolver() {

        return artifactResolver;
    }

    /**
     * 
     * @return
     */
    public final ArtifactRepository getLocalRepository() {

        return localRepository;
    }

    /**
     * 
     * @return
     */
    public final ArtifactFactory getArtifactFactory() {

        return artifactFactory;
    }

    /**
     * 
     * @return
     */
    public final MavenProjectBuilder getProjectBuilder() {

        return projectBuilder;
    }

    /**
     * 
     * @return
     */
    public final List<ArtifactRepository> getRemoteRepositories() {

        return remoteRepositories;
    }

    /**
     * 
     * @return
     */
    public final ArtifactInstaller getArtifactInstaller() {

        return artifactInstaller;
    }

    public PlexusContainer getPlexusContainer() {

        return plexusContainer;
    }

    public void setPlexusContainer( PlexusContainer plexusContainer ) {

        this.plexusContainer = plexusContainer;
    }

    protected <T> T lookup( Class<T> role ) throws Exception {

        return (T) getPlexusContainer().lookup( role );
    }

    protected <T> T lookup( Class<T> role, String id ) throws Exception {

        return (T) getPlexusContainer().lookup( role.getName(), id );
    }

    protected void release( Object component ) throws Exception {

        getPlexusContainer().release( component );
    }

    public void contextualize( Context context ) throws ContextException {

        plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
