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

import generated.xcode.BuildAction;
import generated.xcode.BuildActionEntries;
import generated.xcode.BuildActionEntries.BuildActionEntry;
import generated.xcode.BuildableReference;
import generated.xcode.Scheme;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;

public final class SchemeService {

    private SchemeService() {

        // do nothing
    }

    public static final Scheme unmarshal( File file ) {

        return unmarshal( file, Scheme.class );
    }

    public static final <T> T unmarshal( File file, Class<T> clazz ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate Xcscheme file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, clazz );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal Xcscheme file: " + file );
            throw new IllegalStateException( e );
        }
    }

    public static final BuildableReference getBuildableReference( Scheme scheme, String target ) {

        BuildAction buildAction = scheme.getBuildAction();
        BuildActionEntries _buildActionEntries = buildAction.getBuildActionEntries();
        List<BuildActionEntry> buildActionEntries = _buildActionEntries.getBuildActionEntry();

        for (int i = 0; i < buildActionEntries.size(); i++) {
            BuildActionEntry buildActionEntry = buildActionEntries.get( i );
            BuildableReference buildableReference = buildActionEntry.getBuildableReference();
            if ( target.equals( buildableReference.getBlueprintName() ) ) {
                return buildableReference;
            }
        }

        JoJoMojo.getMojo().getLog().error( "Unable to locate BuildableReference for target: " + target );
        throw new IllegalStateException();
    }

    public static final BuildableReference[] getBuildableReferences( Scheme scheme, boolean archiving ) {

        BuildAction buildAction = scheme.getBuildAction();
        BuildActionEntries _buildActionEntries = buildAction.getBuildActionEntries();
        List<BuildActionEntry> buildActionEntries = _buildActionEntries.getBuildActionEntry();

        List<BuildableReference> buildableReferences = new LinkedList<BuildableReference>();

        for (int i = 0; i < buildActionEntries.size(); i++) {
            BuildActionEntry buildActionEntry = buildActionEntries.get( i );
            if ( archiving && !XCodeService.TRUE_YES.equals( buildActionEntry.getBuildForArchiving() ) ) {
                continue;
            }
            buildableReferences.add( buildActionEntry.getBuildableReference() );
        }

        BuildableReference[] _buildableReferences = new BuildableReference[buildableReferences.size()];
        buildableReferences.toArray( _buildableReferences );
        return _buildableReferences;
    }
}
