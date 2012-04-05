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
package org.hardisonbrewing.maven.cxx.qnx;

import java.io.File;

import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.core.ProjectService;
import org.hardisonbrewing.maven.cxx.qde.CProjectService;

public final class QnxService {

    public static final String PACKAGING_QNX = "qnx";
    public static final String PACKAGING_QDE = "qde";

    private static final String MAKEFILE_FILENAME = "Makefile";

    public static boolean isMakefileBuilder( String target ) {

        if ( CProjectService.getCProject() != null ) {
            return CProjectService.isMakefileBuilder( target );
        }

        return PACKAGING_QNX.equals( JoJoMojo.getMojo().getProject().getPackaging() );
    }

    public static boolean hasMakefile() {

        File file = getMakefile();
        return file.exists();
    }

    public static File getMakefile() {

        return new File( getMakefilePath() );
    }

    public static String getMakefilePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( MAKEFILE_FILENAME );
        return stringBuffer.toString();
    }
}
