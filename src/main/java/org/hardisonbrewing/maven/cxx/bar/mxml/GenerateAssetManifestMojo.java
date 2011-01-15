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

package org.hardisonbrewing.maven.cxx.bar.mxml;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.codehaus.plexus.util.IOUtil;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.bar.PropertiesService;
import org.hardisonbrewing.maven.cxx.bar.TargetDirectoryService;

/**
 * @goal mxml-generate-asset-manifest
 * @phase process-resources
 */
public class GenerateAssetManifestMojo extends JoJoMojoImpl {

    @Override
    public void execute() {

        if ( true || !shouldExecute() ) {
            getLog().info( "assets.xml is up-to-date, not rebuilding!" );
            return;
        }

        getLog().info( "Generating assets.xml..." );

        StringBuffer assetsXmlPath = new StringBuffer();
        assetsXmlPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        assetsXmlPath.append( File.separator );
        assetsXmlPath.append( "assets.xml" );
        DataOutputStream outputStream = null;

        try {
            outputStream = new DataOutputStream( new FileOutputStream( assetsXmlPath.toString() ) );
            outputStream.writeChars( "<?xml version=\"1.0\"?>" );
            outputStream.writeChars( "<componentPackage>" );

            String generatedResourceDirectoryPath = TargetDirectoryService.getGeneratedResourcesDirectoryPath();
            for (File file : TargetDirectoryService.getResourceFiles()) {
                String filePath = FileUtils.getCanonicalPath( file.getPath(), generatedResourceDirectoryPath );
                filePath = filePath.substring( 0, filePath.lastIndexOf( '.' ) );
                filePath = filePath.replace( File.separatorChar, '.' );
                String fileName = file.getName();
                outputStream.writeChars( "<component id=\"" );
                outputStream.writeChars( fileName.substring( 0, fileName.lastIndexOf( '.' ) ) );
                outputStream.writeChars( "\" class=\"" );
                outputStream.writeChars( filePath );
                outputStream.writeChars( "\"/>" );
            }

            outputStream.writeChars( "</componentPackage>" );
        }
        catch (Exception e) {
            getLog().error( "Unable to generate assets.xml" );
            throw new IllegalStateException( e.getMessage() );
        }
        finally {
            IOUtil.close( outputStream );
        }
    }

    protected final boolean shouldExecute() {

        String swcFileName = "assets.xml";

        if ( PropertiesService.propertiesHaveChanged() ) {
            getLog().info( "Properties have changed, rebuilding " + swcFileName + "..." );
            return true;
        }

        StringBuffer outputPath = new StringBuffer();
        outputPath.append( TargetDirectoryService.getTargetDirectoryPath() );
        outputPath.append( File.separator );
        outputPath.append( swcFileName );

        File outputFile = new File( outputPath.toString() );
        if ( outputFile.exists() ) {
            if ( outputFile.lastModified() >= getLatestFileDate() ) {
                return false;
            }
        }

        return true;
    }

    private final long getLatestFileDate() {

        long lastModified = 0;
        for (String filePath : TargetDirectoryService.getResourceFilePaths()) {
            lastModified = Math.max( lastModified, getLatestFileDate( filePath ) );
        }
        return lastModified;
    }

    private final long getLatestFileDate( String filePath ) {

        File sourceFile = new File( filePath );
        return FileUtils.lastModified( sourceFile, false );
    }
}
