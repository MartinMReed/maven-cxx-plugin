/**
 * Copyright (c) 2011 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.generic;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.core.model.ProjectConfiguration;
import org.hardisonbrewing.maven.cxx.PropertiesService;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.component.BuildConfiguration;
import org.hardisonbrewing.maven.cxx.component.ProjectConfigurationFactory;

/**
 * @goal initialize
 * @phase initialize
 */
public final class InitializeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public Source[] sources;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        ProjectConfigurationFactory projectConfigurationFactory = getProjectConfigurationFactory();
        ProjectConfiguration projectConfiguration = projectConfigurationFactory.createProjectConfiguration();
        ProjectService.setProjectConfiguration( projectConfiguration );

        BuildConfiguration buildConfiguration = getBuildConfiguration();
        ProjectService.setSourceDirectory( buildConfiguration.getSourceDirectory() );
        getLog().debug( "Using source directory: " + getProject().getBuild().getSourceDirectory() );

        TargetDirectoryService.ensureTargetDirectoryExists();

        storePropertyDifferences();
        PropertiesService.storeBuildProperties();

        if ( sources != null ) {
            for (Source source : sources) {
                ProjectService.addSourceDirectory( source );
            }
        }
    }

    private final ProjectConfigurationFactory getProjectConfigurationFactory() {

        try {
            return lookup( ProjectConfigurationFactory.class );
        }
        catch (Exception e) {
            getLog().error( "Unable to locate ProjectConfigurationFactory implementation" );
            throw new IllegalStateException();
        }
    }

    private final BuildConfiguration getBuildConfiguration() {

        try {
            return lookup( BuildConfiguration.class );
        }
        catch (Exception e) {
            getLog().debug( "No custom build configuration found... skipping" );
            return null;
        }
    }

    private final void storePropertyDifferences() {

        Properties previousProperties = PropertiesService.loadBuildProperties();
        if ( previousProperties == null ) {
            getLog().debug( "A previous build.properties file was not found... skipping difference check" );
            return;
        }

        Properties currentProperties = PropertiesService.getProperties();

        Properties differenceProperties = new Properties();

        List<Object> keys = new LinkedList<Object>();
        keys.addAll( previousProperties.keySet() );
        keys.addAll( currentProperties.keySet() );

        for (Object key : keys) {
            String previousProperty = previousProperties.getProperty( (String) key );
            String currentProperty = currentProperties.getProperty( (String) key );
            boolean changed = false;
            if ( previousProperty != null || currentProperty != null ) {
                if ( currentProperty == null ) {
                    getLog().debug( key + " is missing from current properties..." );
                    changed = true;
                }
                else if ( previousProperty == null ) {
                    getLog().debug( key + " is missing from previous properties..." );
                    changed = true;
                }
                else if ( !previousProperty.equals( currentProperty ) ) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append( key );
                    stringBuffer.append( " does not match previous property: previous[" );
                    stringBuffer.append( previousProperty );
                    stringBuffer.append( "] vs current[" );
                    stringBuffer.append( currentProperty );
                    stringBuffer.append( "]" );
                    getLog().debug( stringBuffer );
                    changed = true;
                }
            }
            differenceProperties.put( key, Boolean.toString( changed ) );
        }

        PropertiesService.storeBuildDifferenceProperties( differenceProperties );
    }
}
