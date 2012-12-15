/**
 * Copyright (c) 2012 Martin M Reed
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

import generated.xcode.Scheme;
import generated.xcode.Scheme.BuildAction;
import generated.xcode.Scheme.BuildAction.BuildActionEntries;
import generated.xcode.Scheme.BuildAction.BuildActionEntries.BuildActionEntry;
import generated.xcode.Scheme.BuildAction.BuildActionEntries.BuildActionEntry.BuildableReference;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-convert-xcscheme
 * @phase compile
 */
public final class ConvertXcschemeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String schemeName = this.scheme;
        if ( schemeName == null ) {
            return;
        }

        String filePath = XCodeService.getSchemePath( schemeName );
        File file = new File( filePath );

        Scheme scheme = readScheme( file );
        String[] targets = getSchemeTargets( scheme );

        Properties properties = PropertiesService.getXCodeProperties();

        ConvertPbxprojMojo.putTargets( targets, properties );

        for (Object key : properties.keySet()) {
            getLog().info( key + ": " + properties.getProperty( (String) key ) );
        }

        PropertiesService.storeXCodeProperties( properties );
    }

    private String[] getSchemeTargets( Scheme scheme ) {

        BuildAction buildAction = scheme.getBuildAction();
        BuildActionEntries _buildActionEntries = buildAction.getBuildActionEntries();
        List<BuildActionEntry> buildActionEntries = _buildActionEntries.getBuildActionEntry();

        String[] targets = new String[buildActionEntries.size()];

        for (int i = 0; i < buildActionEntries.size(); i++) {
            BuildActionEntry buildActionEntry = buildActionEntries.get( i );
            BuildableReference buildableReference = buildActionEntry.getBuildableReference();
            targets[i] = buildableReference.getBlueprintName();
        }

        return targets;
    }

    private Scheme readScheme( File file ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate Xcscheme file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, Scheme.class );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal Xcscheme file: " + file );
            throw new IllegalStateException( e );
        }
    }
}
