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

import generated.Array;
import generated.Dict;
import generated.Plist;

import java.util.List;

public class PlistService {

    public static final String getString( Plist plist, String key ) {

        generated.String value = (generated.String) getValue( plist, key );
        return value.getvalue();
    }

    public static final Object getValue( Plist plist, String key ) {

        if ( key == null ) {
            return null;
        }
        List<Object> values = plist.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        if ( values.isEmpty() ) {
            return null;
        }
        if ( values.size() == 1 ) {
            Dict dict = (Dict) values.get( 0 );
            values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        }
        for (int i = 0; i < values.size() - 1; i++) {
            Object value = values.get( i );
            if ( value instanceof generated.Key ) {
                String _key = ( (generated.Key) value ).getvalue();
                if ( key.equals( _key ) ) {
                    return values.get( i + 1 );
                }
            }
        }
        return null;
    }

    public static final void add( Array array, Object value ) {

        List<Object> values = array.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        values.add( value );
    }

    public static final void add( Dict dict, String key, Object value ) {

        List<Object> values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();

        generated.Key _key = new generated.Key();
        _key.setvalue( key );
        values.add( _key );

        values.add( value );
    }

    public static final void add( Plist plist, Object value ) {

        List<Object> values = plist.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        values.add( value );
    }

    public static final boolean setString( Plist plist, String key, String value ) {

        generated.String _value = new generated.String();
        _value.setvalue( value );
        return setValue( plist, key, _value );
    }

    public static final boolean setValue( Plist plist, String key, Object value ) {

        if ( key == null ) {
            return false;
        }
        List<Object> values = plist.getArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        if ( values.isEmpty() ) {
            return false;
        }
        if ( values.size() == 1 ) {
            Dict dict = (Dict) values.get( 0 );
            values = dict.getKeyOrArrayOrDataOrDateOrDictOrRealOrIntegerOrStringOrTrueOrFalse();
        }
        for (int i = 0; i < values.size() - 1; i++) {
            Object _value = values.get( i );
            if ( _value instanceof generated.Key ) {
                String _key = ( (generated.Key) _value ).getvalue();
                if ( key.equals( _key ) ) {
                    values.set( i + 1, value );
                    return true;
                }
            }
        }
        return false;
    }
}
