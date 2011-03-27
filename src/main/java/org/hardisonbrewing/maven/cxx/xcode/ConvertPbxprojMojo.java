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

package org.hardisonbrewing.maven.cxx.xcode;

import generated.Dict;
import generated.Plist;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.TargetDirectoryService;

/**
 * @goal xcode-convert-pbxproj
 * @phase xcode-convert-pbxproj
 */
public final class ConvertPbxprojMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String provisioningProfile;

    @Override
    public final void execute() {

        File plistFile = getPlistFile();
        generatePlistFile( plistFile );

        Plist plist = PlistService.readPlist( plistFile );
        Properties properties = buildProperties( plist );

        for (Object key : properties.keySet()) {
            getLog().info( key + ": " + properties.getProperty( (String) key ) );
        }

        PropertiesService.storeXCodeProperties( properties );
    }

    private File getPlistFile() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( XCodeService.getProject() );
        stringBuffer.append( ".plist" );
        return new File( stringBuffer.toString() );
    }

    private Properties buildProperties( Plist plist ) {

        Properties properties = new Properties();

        Dict root = (Dict) PlistService.getRoot( plist );
        Dict objects = (Dict) PlistService.getValue( root, "objects" );

        List<Object> objectsValues = objects.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        for (int i = 0; i < objectsValues.size() - 1; i += 2) {

            Dict dict = (Dict) objectsValues.get( i + 1 );

            String isa = PlistService.getString( dict, "isa" );
            if ( "XCBuildConfiguration".equals( isa ) ) {
                String config = PlistService.getString( dict, "name" );

                Dict buildSettings = PlistService.getDict( dict, "buildSettings" );
                if ( buildSettings != null ) {
                    String infoPlistFile = PlistService.getString( buildSettings, "INFOPLIST_FILE" );
                    if ( infoPlistFile != null ) {

                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append( config );
                        stringBuffer.append( "." );
                        stringBuffer.append( "infoPlistFile" );
                        String infoPlistFileKey = stringBuffer.toString();

                        properties.put( infoPlistFileKey, infoPlistFile );
                    }
                }
            }

            if ( "PBXProject".equals( isa ) || "PBXNativeTarget".equals( isa ) ) {

                String buildConfigurationListId = PlistService.getString( dict, "buildConfigurationList" );

                String defaultConfigurationNameKey = "defaultConfigurationName";
                if ( "PBXNativeTarget".equals( isa ) ) {
                    String target = PlistService.getString( dict, "name" );
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append( target );
                    stringBuffer.append( "." );
                    stringBuffer.append( defaultConfigurationNameKey );
                    defaultConfigurationNameKey = stringBuffer.toString();
                }

                Dict buildConfigurationList = (Dict) PlistService.getValue( objects, buildConfigurationListId );
                String defaultConfigurationName = PlistService.getString( buildConfigurationList, "defaultConfigurationName" );
                properties.put( defaultConfigurationNameKey, defaultConfigurationName );
            }
        }

        return properties;
    }

    private void generatePlistFile( File file ) {

        String pbxprojFilePath = XCodeService.getPbxprojPath();

        List<String> cmd = new LinkedList<String>();
        cmd.add( "plutil" );
        cmd.add( "-convert" );
        cmd.add( "xml1" );
        cmd.add( "-o" );
        cmd.add( file.getAbsolutePath() );
        cmd.add( pbxprojFilePath );
        execute( cmd );
    }
}
