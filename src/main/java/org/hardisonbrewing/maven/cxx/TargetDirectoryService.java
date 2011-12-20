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
package org.hardisonbrewing.maven.cxx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;

public class TargetDirectoryService extends org.hardisonbrewing.maven.core.TargetDirectoryService {

    public static final String TARGET_CLEANUP_FILE = "target_directories.txt";
    public static final String SOURCES_DIRECTORY = "generated-sources";
    public static final String RESOURCES_DIRECTORY = "generated-resources";
    public static final String PROCESSED_SOURCES_DIRECTORY = "processed-sources";

    protected TargetDirectoryService() {

        // do nothing
    }

    public static final String[] getTargetCleaupDirectories() {

        File file = getTargetCleanupFile();

        if ( !file.exists() ) {
            return null;
        }

        String contents;

        try {
            contents = FileUtils.fileRead( file );
        }
        catch (Exception e) {
            JoJoMojo.getMojo().getLog().error( "Unable to read target cleanup directory file: " + file );
            throw new IllegalStateException();
        }

        return contents.split( "\n\r" );
    }

    public static final void registerTargetDirectory( String dirPath ) {

        File file = getTargetCleanupFile();

        boolean append = file.exists();

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream( file, append );
            if ( append ) {
                outputStream.write( "\n".getBytes() );
            }
            outputStream.write( dirPath.getBytes() );
        }
        catch (Exception e) {
            JoJoMojo.getMojo().getLog().error( "Unable to write target directory file: " + file );
            throw new IllegalStateException();
        }
    }

    public static String getTargetCleanupFilePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( TARGET_CLEANUP_FILE );
        return stringBuffer.toString();
    }

    public static File getTargetCleanupFile() {

        return new File( getTargetCleanupFilePath() );
    }

    private static String getTargetDirectoryPath( String type ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( type );
        return stringBuffer.toString();
    }

    public static String getGeneratedSourcesDirectoryPath() {

        return getTargetDirectoryPath( SOURCES_DIRECTORY );
    }

    public static File getGeneratedSourcesDirectory() {

        return new File( getGeneratedSourcesDirectoryPath() );
    }

    public static String getGeneratedResourcesDirectoryPath() {

        return getTargetDirectoryPath( RESOURCES_DIRECTORY );
    }

    public static File getGeneratedResourcesDirectory() {

        return new File( getGeneratedResourcesDirectoryPath() );
    }

    public static String getProcessedSourcesDirectoryPath() {

        return getTargetDirectoryPath( PROCESSED_SOURCES_DIRECTORY );
    }

    public static File getProcessedSourcesDirectory() {

        return new File( getProcessedSourcesDirectoryPath() );
    }

    public static final String[] getSourceFilePaths() {

        return FileUtils.listFilePathsRecursive( getGeneratedSourcesDirectory() );
    }

    public static final File[] getSourceFiles() {

        return FileUtils.listFilesRecursive( getGeneratedSourcesDirectory() );
    }

    public static final String[] getResourceFilePaths() {

        return FileUtils.listFilePathsRecursive( getGeneratedResourcesDirectory() );
    }

    public static final File[] getResourceFiles() {

        return FileUtils.listFilesRecursive( getGeneratedResourcesDirectory() );
    }

    public static final String[] getProcessableSourceFilePaths() {

        String processedSourcesDirectoryPath = getProcessedSourcesDirectoryPath();
        String generatedSourcesDirectoryPath = getGeneratedSourcesDirectoryPath();

        String[] sourceFilePaths = getSourceFilePaths();

        for (int i = 0; i < sourceFilePaths.length; i++) {
            StringBuffer sourceFilePath = new StringBuffer();
            sourceFilePath.append( processedSourcesDirectoryPath );
            sourceFilePath.append( File.separator );
            sourceFilePath.append( FileUtils.getCanonicalPath( sourceFilePaths[i], generatedSourcesDirectoryPath ) );
            sourceFilePaths[i] = sourceFilePath.toString();
        }

        return sourceFilePaths;
    }

    public static final String[] getProcessedSourceFilePaths() {

        return FileUtils.listFilePathsRecursive( getProcessedSourcesDirectory() );
    }

    public static final File[] getProcessedSourceFiles() {

        return FileUtils.listFilesRecursive( getProcessedSourcesDirectory() );
    }

    public static final String resolveProcessedFilePath( String filePath ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getProcessedSourcesDirectory() );
        stringBuffer.append( File.separator );
        stringBuffer.append( FileUtils.getProjectCanonicalPath( filePath ) );
        return stringBuffer.toString();
    }
}
