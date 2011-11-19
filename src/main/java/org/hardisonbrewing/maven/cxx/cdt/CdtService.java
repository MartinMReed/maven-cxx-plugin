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
package org.hardisonbrewing.maven.cxx.cdt;

import generated.org.eclipse.cdt.Plugin;
import generated.org.eclipse.cdt.Plugin.Extension;
import generated.org.eclipse.cdt.Plugin.Extension.ContentType;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.util.IOUtil;
import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;

public class CdtService {

    private static String eclipseDirPath;
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

    public static final String getEclipsePluginsDirPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getEclipseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "plugins" );
        return stringBuffer.toString();
    }

    public static final String getCdtCoreJarPath() {

        File baseDir = new File( getEclipsePluginsDirPath() );

        String[] includes = new String[] { "org.eclipse.cdt.core_*.jar" };
        String[] files = FileUtils.listFilePathsRecursive( baseDir, includes, null );

        if ( files.length > 0 ) {
            return files[0];
        }

        return null;
    }

    public static final Plugin getCdtCorePlugin() {

        InputStream inputStream = null;

        String jarFilePath = getCdtCoreJarPath();

        try {
            JarFile jarFile = new JarFile( jarFilePath );
            ZipEntry zipEntry = jarFile.getEntry( "plugin.xml" );
            inputStream = jarFile.getInputStream( zipEntry );
            return JAXB.unmarshal( inputStream, Plugin.class );
        }
        catch (Exception e) {
            JoJoMojo.getMojo().getLog().error( "Unable to load JAR: " + jarFilePath );
            throw new IllegalStateException();
        }
        finally {
            IOUtil.close( inputStream );
        }
    }

    public static String getEclipseDirPath() {

        return eclipseDirPath;
    }

    public static void setEclipseDirPath( String eclipseDirPath ) {

        CdtService.eclipseDirPath = eclipseDirPath;
    }

    public static String[] getSourceExtensions() {

        return sourceExtensions;
    }

    public static String[] getResourceExtensions() {

        return resourceExtensions;
    }
}
