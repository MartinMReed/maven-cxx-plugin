/**
 * Copyright (c) 2013 Martin M Reed
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

import generated.xcode.BuildableReference;
import generated.xcode.Scheme;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-convert-xcscheme
 * @phase initialize
 */
public final class ConvertSchemeMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String scheme;

    /**
     * @parameter
     */
    public String action;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        File schemeFile = XCodeService.findXcscheme( this.scheme );
        Scheme scheme = SchemeService.unmarshal( schemeFile );

        boolean archiving = XCodeService.ACTION_ARCHIVE.equals( action );
        BuildableReference[] buildableReferences = SchemeService.getBuildableReferences( scheme, archiving );
        String[] targets = getTargets( buildableReferences );

        Properties properties = PropertiesService.getXCodeProperties();
        ConvertPbxprojMojo.putTargets( targets, properties );
        PropertiesService.storeXCodeProperties( properties );
    }

    private String[] getTargets( BuildableReference[] buildableReferences ) {

        List<String> targets = new LinkedList<String>();

        for (int i = 0; i < buildableReferences.length; i++) {
            BuildableReference buildableReference = buildableReferences[i];
            targets.add( buildableReference.getBlueprintName() );
        }

        String[] _targets = new String[targets.size()];
        targets.toArray( _targets );
        return _targets;
    }
}
