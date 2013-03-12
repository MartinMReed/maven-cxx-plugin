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
package org.hardisonbrewing.maven.cxx.msbuild;

import java.io.File;

public class TargetDirectoryService extends org.hardisonbrewing.maven.core.TargetDirectoryService {

    protected TargetDirectoryService() {

        // do nothing
    }

    public static final File getTestCoverageFile() {

        return new File( getTestCoveragePath() );
    }

    public static final String getTestCoveragePath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "test.coverage" );
        return stringBuffer.toString();
    }

    public static final String getTestResultPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "test-report.xml" );
        return stringBuffer.toString();
    }

    public static final String getBinDirectoryPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( "bin" );
        return stringBuffer.toString();
    }

    public static final String getAssemblyInfoGenPath() {

        return getAssemblyInfoPath( "gen" );
    }

    public static final File getAssemblyInfoGenFile() {

        return new File( getAssemblyInfoGenPath() );
    }

    public static final String getAssemblyInfoBakPath() {

        return getAssemblyInfoPath( "bak" );
    }

    public static final File getAssemblyInfoBakFile() {

        return new File( getAssemblyInfoBakPath() );
    }

    private static final String getAssemblyInfoPath( String ext ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( MSBuildService.ASSEMBLY_INFO_CS );
        stringBuffer.append( "." );
        stringBuffer.append( ext );
        return stringBuffer.toString();
    }
}
