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

import generated.plist.Array;
import generated.plist.Dict;
import generated.plist.Plist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.JoJoMojo;

public final class PlistService {

    private PlistService() {

        // do nothing
    }

    public static final Plist readPlist( File file ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate PLIST file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, Plist.class );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal PLIST file: " + file );
            throw new IllegalStateException( e );
        }
    }

    public static final void writePlist( Plist plist, File file ) {

        try {
            JAXB.marshal( file, plist );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to marshal PLIST file: " + file );
            throw new IllegalStateException( e );
        }
    }

    public static final List<String> getStringArray( Dict dict, String key ) {

        List<Object> value = getArray( dict, key );
        if ( value == null ) {
            return null;
        }
        List<String> array = new ArrayList<String>();
        for (Object object : value) {
            generated.plist.String string = (generated.plist.String) object;
            array.add( string.getvalue() );
        }
        return array;
    }

    public static final List<Object> getArray( Dict dict, String key ) {

        generated.plist.Array value = (generated.plist.Array) getValue( dict, key );
        if ( value == null ) {
            return null;
        }
        return value.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
    }

    public static final String getString( Dict dict, String key ) {

        generated.plist.String value = (generated.plist.String) getValue( dict, key );
        if ( value == null ) {
            return null;
        }
        return value.getvalue();
    }

    public static final Dict getDict( Dict dict, String key ) {

        return (Dict) getValue( dict, key );
    }

    public static final Object getValue( Dict dict, String key ) {

        List<Object> values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        for (int i = 0; i < values.size() - 1; i += 2) {
            generated.plist.Key _key = (generated.plist.Key) values.get( i );
            if ( key.equals( _key.getvalue() ) ) {
                return values.get( i + 1 );
            }
        }
        return null;
    }

    public static final List<String> getKeys( Dict dict ) {

        List<String> keys = new ArrayList<String>();
        List<Object> values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        for (int i = 0; i < values.size() - 1; i += 2) {
            generated.plist.Key _key = (generated.plist.Key) values.get( i );
            keys.add( _key.getvalue() );
        }
        return keys;
    }

    public static final Object getRoot( Plist plist ) {

        List<Object> values = plist.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        if ( values.isEmpty() ) {
            return null;
        }
        if ( values.size() == 1 ) {
            return values.get( 0 );
        }
        return values;
    }

    public static final void add( Array array, Object value ) {

        List<Object> values = array.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        values.add( value );
    }

    public static final void add( Dict dict, String key, Object value ) {

        List<Object> values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();

        generated.plist.Key _key = new generated.plist.Key();
        _key.setvalue( key );
        values.add( _key );

        values.add( value );
    }

    public static final void add( Plist plist, Object value ) {

        List<Object> values = plist.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        values.add( value );
    }
}
