/**
 * Copyright (c) 2010-2013 Martin M Reed
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

import generated.plist.Dict;
import generated.plist.Plist;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;

/**
 * @goal xcode-convert-pbxproj
 * @phase initialize
 */
public final class ConvertPbxprojMojo extends JoJoMojoImpl {

    private static final String PROP_VAL_NAME = "name";
    private static final String PROP_VAL_PATH = "path";

    /**
     * @parameter
     */
    public String[] targetIncludes;

    /**
     * @parameter
     */
    public String[] targetExcludes;

    /**
     * @parameter
     */
    public String scheme;

    private final Hashtable<String, String> groupIndex = new Hashtable<String, String>();
    private final Hashtable<String, String> fileIndex = new Hashtable<String, String>();
    private final Hashtable<String, Dict> keyIndex = new Hashtable<String, Dict>();
    private final Hashtable<String, Vector<String>> isaIndex = new Hashtable<String, Vector<String>>();

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        File plistFile = getPlistFile();
        generatePlistFile( plistFile );

        Plist plist = PlistService.readPlist( plistFile );
        indexProperties( plist );

        Properties properties = PropertiesService.getXCodeProperties();

        buildProperties( properties );

        for (Object key : properties.keySet()) {
            getLog().info( key + ": " + properties.getProperty( (String) key ) );
        }

        PropertiesService.storeXCodeProperties( properties );
    }

    private File getPlistFile() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "project.plist" );
        return new File( stringBuffer.toString() );
    }

    private void buildProperties( Properties properties ) {

        for (String key : isaIndex.get( "XCBuildConfiguration" )) {

            Dict dict = keyIndex.get( key );

            Dict buildSettings = PlistService.getDict( dict, "buildSettings" );
            if ( buildSettings == null ) {
                continue;
            }

            String infoPlistFile = PlistService.getString( buildSettings, "INFOPLIST_FILE" );
            if ( infoPlistFile == null ) {
                continue;
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( PlistService.getString( dict, PROP_VAL_NAME ) );
            stringBuffer.append( ".infoPlistFile" );
            String infoPlistFileKey = stringBuffer.toString();

            properties.put( infoPlistFileKey, infoPlistFile );
        }

        for (String key : isaIndex.get( "PBXGroup" )) {
            Dict dict = keyIndex.get( key );
            List<String> children = PlistService.getStringArray( dict, "children" );
            for (String child : children) {
                groupIndex.put( child, key );
            }
        }

        for (String key : isaIndex.get( "PBXProject" )) {
            Dict dict = keyIndex.get( key );
            putDefaultCongurationName( dict, properties );
            if ( scheme == null ) {
                putTargets( dict, properties );
            }
        }

        for (String key : isaIndex.get( "PBXNativeTarget" )) {
            Dict dict = keyIndex.get( key );
            putTargetDefaultCongurationName( dict, properties );
            putTargetProductType( dict, properties );
            putTargetProductReference( dict, properties );
        }

        for (String key : isaIndex.get( "PBXFileReference" )) {
            Dict dict = keyIndex.get( key );
            String name = PlistService.getString( dict, PROP_VAL_NAME );
            if ( name == null ) {
                name = PlistService.getString( dict, PROP_VAL_PATH );
            }
            String path = resolvePath( key );
            fileIndex.put( name, path );
        }
        XCodeService.setFileIndex( fileIndex );
    }

    private String resolvePath( String key ) {

        StringBuffer stringBuffer = new StringBuffer();
        while (key != null) {
            Dict dict = keyIndex.get( key );
            String path = PlistService.getString( dict, PROP_VAL_PATH );
            if ( path != null ) {
                if ( stringBuffer.length() != 0 ) {
                    stringBuffer.insert( 0, File.separator );
                }
                stringBuffer.insert( 0, path );
            }
            key = groupIndex.get( key );
        }
        return stringBuffer.toString();
    }

    private void putTargets( Dict dict, Properties properties ) {

        String[] targets = getTargets( dict );
        putTargets( targets, properties );
    }

    private String[] getTargets( Dict dict ) {

        List<String> targets = PlistService.getStringArray( dict, XCodeService.PROP_TARGETS );

        for (int i = 0; i < targets.size(); i++) {
            Dict productReference = keyIndex.get( targets.get( i ) );
            targets.set( i, PlistService.getString( productReference, PROP_VAL_NAME ) );
        }

        if ( targetIncludes != null || targetExcludes != null ) {

            List<String> _targets = new ArrayList<String>();

            if ( targetIncludes != null ) {
                for (String target : targets) {
                    include_loop: for (String include : targetIncludes) {
                        if ( target.equalsIgnoreCase( include ) ) {
                            _targets.add( target );
                            break include_loop;
                        }
                    }
                }
            }
            else {
                for (String target : targets) {
                    boolean match = false;
                    exclude_loop: for (String exclude : targetExcludes) {
                        if ( target.equalsIgnoreCase( exclude ) ) {
                            match = true;
                            break exclude_loop;
                        }
                    }
                    if ( !match ) {
                        _targets.add( target );
                    }
                }
            }

            targets = _targets;
        }

        String[] _targets = new String[targets.size()];
        targets.toArray( _targets );
        return _targets;
    }

    public static void putTargets( String[] targets, Properties properties ) {

        XCodeService.setTargets( targets );

        String _targets = "";
        for (String target : targets) {
            if ( !_targets.isEmpty() ) {
                _targets += ",";
            }
            _targets += target;
        }
        properties.put( XCodeService.PROP_TARGETS, _targets );
    }

    private void putDefaultCongurationName( Dict dict, Properties properties ) {

        String buildConfigurationListId = PlistService.getString( dict, XCodeService.PROP_BUILD_CONFIG_LIST );
        Dict buildConfigurationList = keyIndex.get( buildConfigurationListId );
        String defaultConfigurationName = PlistService.getString( buildConfigurationList, XCodeService.PROP_DEFAULT_CONFIG_NAME );
        properties.put( XCodeService.PROP_DEFAULT_CONFIG_NAME, defaultConfigurationName );
    }

    private void putTargetProductType( Dict dict, Properties properties ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PlistService.getString( dict, PROP_VAL_NAME ) );
        stringBuffer.append( "." );
        stringBuffer.append( XCodeService.PROP_PRODUCT_TYPE );
        String propertyKey = stringBuffer.toString();

        String productType = PlistService.getString( dict, XCodeService.PROP_PRODUCT_TYPE );
        properties.put( propertyKey, productType );
    }

    private void putTargetProductReference( Dict dict, Properties properties ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PlistService.getString( dict, PROP_VAL_NAME ) );
        stringBuffer.append( "." );
        stringBuffer.append( XCodeService.PROP_PRODUCT_REFERENCE );
        String propertyKey = stringBuffer.toString();

        String productReferenceId = PlistService.getString( dict, XCodeService.PROP_PRODUCT_REFERENCE );
        Dict productReference = keyIndex.get( productReferenceId );
        String productReferencePath = PlistService.getString( productReference, PROP_VAL_PATH );
        properties.put( propertyKey, productReferencePath );
    }

    private void putTargetDefaultCongurationName( Dict dict, Properties properties ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( PlistService.getString( dict, PROP_VAL_NAME ) );
        stringBuffer.append( "." );
        stringBuffer.append( XCodeService.PROP_DEFAULT_CONFIG_NAME );
        String propertyKey = stringBuffer.toString();

        String buildConfigurationListId = PlistService.getString( dict, XCodeService.PROP_BUILD_CONFIG_LIST );
        Dict buildConfigurationList = keyIndex.get( buildConfigurationListId );
        String defaultConfigurationName = PlistService.getString( buildConfigurationList, XCodeService.PROP_DEFAULT_CONFIG_NAME );
        if ( defaultConfigurationName == null ) {
            return;
        }

        properties.put( propertyKey, defaultConfigurationName );
    }

    private void indexProperties( Plist plist ) {

        Dict root = (Dict) PlistService.getRoot( plist );
        Dict objects = (Dict) PlistService.getValue( root, "objects" );

        List<Object> objectsValues = objects.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        for (int i = 0; i < objectsValues.size() - 1; i += 2) {

            generated.plist.Key key = (generated.plist.Key) objectsValues.get( i );
            Dict dict = (Dict) objectsValues.get( i + 1 );
            keyIndex.put( key.getvalue(), dict );

            String isa = PlistService.getString( dict, "isa" );
            if ( isa != null ) {
                Vector<String> keys = isaIndex.get( isa );
                if ( keys == null ) {
                    keys = new Vector<String>();
                }
                keys.add( key.getvalue() );
                isaIndex.put( isa, keys );
            }
        }
    }

    private void generatePlistFile( File file ) {

        List<String> cmd = new LinkedList<String>();
        cmd.add( "plutil" );
        cmd.add( "-convert" );
        cmd.add( "xml1" );
        cmd.add( "-o" );
        cmd.add( file.getAbsolutePath() );
        cmd.add( XCodeService.getPbxprojPath() );
        execute( cmd );
    }
}
