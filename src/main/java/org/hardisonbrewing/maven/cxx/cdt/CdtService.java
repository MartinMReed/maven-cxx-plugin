/**
 * Copyright (c) 2011-2012 Martin M Reed
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
package org.hardisonbrewing.maven.cxx.cdt;

import generated.org.eclipse.cdt.Plugin;
import generated.org.eclipse.cdt.Plugin.Extension;
import generated.org.eclipse.cdt.Plugin.Extension.ContentType;
import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.IOUtil;
import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.QccToolChain;
import org.hardisonbrewing.maven.cxx.cdt.toolchain.ToolChain;

public class CdtService {

    private static String[] sourceExtensions;
    private static String[] resourceExtensions;

    public static void loadCdtCoreFileExtensions() {

        Plugin plugin = getCdtCorePlugin();

        List<Extension> extensions = plugin.getExtension();
        if ( extensions == null ) {
            return;
        }

        List<String> sourceExtensions = new ArrayList<String>();
        List<String> resourceExtensions = new ArrayList<String>();

        for (Extension extension : extensions) {

            if ( !"org.eclipse.core.contenttype.contentTypes".equals( extension.getPoint() ) ) {
                continue;
            }

            List<ContentType> contentTypes = extension.getContentType();
            if ( contentTypes == null ) {
                continue;
            }

            for (ContentType contentType : contentTypes) {

                String fileExtensions = contentType.getFileExtensions();
                if ( fileExtensions == null ) {
                    continue;
                }

                String id = contentType.getId();

                boolean resource = id.endsWith( "Header" );
                boolean source = id.endsWith( "Source" );

                for (String fileExtension : fileExtensions.split( "," )) {
                    if ( source ) {
                        sourceExtensions.add( fileExtension );
                    }
                    else if ( resource ) {
                        resourceExtensions.add( fileExtension );
                    }
                }
            }
        }

        if ( !sourceExtensions.isEmpty() ) {
            String[] _sourceExtensions = new String[sourceExtensions.size()];
            sourceExtensions.toArray( _sourceExtensions );
            CdtService.sourceExtensions = _sourceExtensions;
        }

        if ( !resourceExtensions.isEmpty() ) {
            String[] _resourceExtensions = new String[resourceExtensions.size()];
            resourceExtensions.toArray( _resourceExtensions );
            CdtService.resourceExtensions = _resourceExtensions;
        }
    }

    public static final Plugin getCdtCorePlugin() {

        InputStream inputStream = null;

        try {
            inputStream = CdtService.class.getResourceAsStream( "/cdt/plugin.xml" );
            return JAXB.unmarshal( inputStream, Plugin.class );
        }
        catch (Exception e) {
            JoJoMojo.getMojo().getLog().error( "Unable to parse plugin.xml" );
            throw new IllegalStateException();
        }
        finally {
            IOUtil.close( inputStream );
        }
    }

    public static ToolChain getToolChain( Configuration configuration ) {

        String id = configuration.getId();

        if ( QccToolChain.matches( configuration ) ) {
            return new QccToolChain( configuration );
        }

        JoJoMojo.getMojo().getLog().error( "Unsupported toolchain[" + id + "]" );
        throw new UnsupportedOperationException();
    }

    public static String[] getSourceExtensions() {

        return sourceExtensions;
    }

    public static String[] getResourceExtensions() {

        return resourceExtensions;
    }
}
