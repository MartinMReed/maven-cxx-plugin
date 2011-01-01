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

package org.hardisonbrewing.maven.cxx.generic;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.core.TargetDirectoryService;
import org.hardisonbrewing.maven.cxx.PropertiesService;
import org.hardisonbrewing.maven.cxx.component.BuildConfiguration;

/**
 * @goal validate
 * @phase validate
 * @requiresDependencyResolution validate
 */
public final class ValidateMojo extends JoJoMojoImpl {

    @Override
    public final void execute() {

        updateBuildConfiguration();

        String[] resourceFilePaths = TargetDirectoryService.getResourceFilePaths();
        for (int i = 0; resourceFilePaths != null && i < resourceFilePaths.length; i++) {
            validate( resourceFilePaths[i] );
        }

        TargetDirectoryService.ensureTargetDirectoryExists();

        storePropertyDifferences();
        PropertiesService.storeBuildProperties();
    }

    private final void storePropertyDifferences() {

        Properties previousProperties = PropertiesService.loadBuildProperties();
        if ( previousProperties == null ) {
            getLog().info( "A previous build.properties file was not found... skipping difference check" );
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
                    getLog().info( key + " is missing from current properties..." );
                    changed = true;
                }
                else if ( previousProperty == null ) {
                    getLog().info( key + " is missing from previous properties..." );
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
                    getLog().info( stringBuffer );
                    changed = true;
                }
            }
            differenceProperties.put( key, Boolean.toString( changed ) );
        }

        PropertiesService.storeBuildDifferenceProperties( differenceProperties );
    }

    private final void updateBuildConfiguration() {

        BuildConfiguration buildConfiguration = null;

        try {
            buildConfiguration = lookup( BuildConfiguration.class, getProject().getPackaging() );
        }
        catch (Exception e) {
            getLog().debug( "No custom build configuration found... skipping" );
            return;
        }

        if ( buildConfiguration != null ) {
            getLog().info( "Using source directory: " + buildConfiguration.getSourceDirectory() );
            getProject().getBuild().setSourceDirectory( buildConfiguration.getSourceDirectory() );
        }
    }

    public static final void validate( String fileName ) {

        if ( fileName.startsWith( FileUtils.PARENT_DIRECTORY_MARKER ) ) {
            throw new IllegalArgumentException( "File[" + fileName + "] is outside the project domain." );
        }
    }
}
