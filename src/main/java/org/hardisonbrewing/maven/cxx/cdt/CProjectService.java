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

import generated.org.eclipse.cdt.Cproject;
import generated.org.eclipse.cdt.Entry;
import generated.org.eclipse.cdt.ListOptionValue;
import generated.org.eclipse.cdt.Option;
import generated.org.eclipse.cdt.StorageModule;
import generated.org.eclipse.cdt.StorageModule.Cconfiguration;
import generated.org.eclipse.cdt.StorageModule.Configuration;
import generated.org.eclipse.cdt.StorageModule.Configuration.FolderInfo;
import generated.org.eclipse.cdt.StorageModule.Configuration.SourceEntries;
import generated.org.eclipse.cdt.ToolChain;
import generated.org.eclipse.cdt.ToolChain.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;

public class CProjectService {

    public static final String CPROJECT_FILENAME = ".cproject";

    public static final String MODULE_SETTINGS = "org.eclipse.cdt.core.settings";

    private static final String CDT_BUILD_SYSTEM = "cdtBuildSystem";

    private static final String BUILD_TYPE = "org.eclipse.cdt.build.core.buildType";
    private static final String BUILD_TYPE_RELEASE = BUILD_TYPE + ".release";
    private static final String BUILD_TYPE_DEBUG = BUILD_TYPE + ".debug";

    private static final String BUILD_ARTIFACT_TYPE = "org.eclipse.cdt.build.core.buildArtefactType";
    private static final String BUILD_ARTIFACT_TYPE_EXE = BUILD_ARTIFACT_TYPE + ".exe";

    private static final String SOURCE_KIND_PATH = "sourcePath";

    public static Cproject readCProject( File file ) {

        return JAXB.unmarshal( file, Cproject.class );
    }

    public static List<Cconfiguration> getCconfigurations( Cproject cproject, String module ) {

        for (StorageModule storageModule : cproject.getStorageModule()) {
            if ( module.equals( storageModule.getModuleId() ) ) {
                return storageModule.getCconfiguration();
            }
        }

        return null;
    }

    public static Configuration getBuildConfiguration( Cconfiguration cconfiguration ) {

        for (StorageModule storageModule : cconfiguration.getStorageModule()) {
            if ( CDT_BUILD_SYSTEM.equals( storageModule.getModuleId() ) ) {
                return storageModule.getConfiguration();
            }
        }

        return null;
    }

    public static String[] getSourcePaths( Configuration configuration ) {

        SourceEntries sourceEntries = configuration.getSourceEntries();

        List<String> sourcePaths = new ArrayList<String>();

        for (Entry entry : sourceEntries.getEntry()) {
            if ( SOURCE_KIND_PATH.equals( entry.getKind() ) ) {
                sourcePaths.add( entry.getName() );
            }
        }

        String[] _sourcePaths = new String[sourcePaths.size()];
        sourcePaths.toArray( _sourcePaths );
        return _sourcePaths;
    }

    public static ToolChain getToolChain( Configuration configuration ) {

        List<FolderInfo> folderInfos = configuration.getFolderInfo();
        FolderInfo folderInfo = folderInfos.get( 0 );
        List<ToolChain> toolChains = folderInfo.getToolChain();
        return toolChains.get( 0 );
    }

    public static Tool getTool( ToolChain toolChain, String superClass ) {

        for (Tool tool : toolChain.getTool()) {
            if ( superClass.equals( tool.getSuperClass() ) ) {
                return tool;
            }
        }
        return null;
    }

    public static Option getToolOption( Tool tool, String superClass ) {

        for (Option option : tool.getOption()) {
            if ( superClass.equals( option.getSuperClass() ) ) {
                return option;
            }
        }
        return null;
    }

    public static String getToolOptionValue( Tool tool, String superClass ) {

        Option option = getToolOption( tool, superClass );
        if ( option == null ) {
            return null;
        }
        return option.getValue();
    }

    public static String[] getToolOptionValues( Tool tool, String superClass ) {

        Option option = getToolOption( tool, superClass );
        if ( option == null ) {
            return null;
        }
        return getOptionValues( option );
    }

    public static Option getToolChainOption( ToolChain toolChain, String superClass ) {

        for (Option option : toolChain.getOption()) {
            if ( superClass.equals( option.getSuperClass() ) ) {
                return option;
            }
        }
        return null;
    }

    public static String getToolChainOptionValue( ToolChain toolChain, String superClass ) {

        Option option = getToolChainOption( toolChain, superClass );
        if ( option == null ) {
            return null;
        }
        return option.getValue();
    }

    public static String[] getOptionValues( Option option ) {

        List<ListOptionValue> listOptionValues = option.getListOptionValue();
        String[] values = new String[listOptionValues.size()];

        for (int i = 0; i < values.length; i++) {
            ListOptionValue listOptionValue = listOptionValues.get( i );
            values[i] = listOptionValue.getValue();
        }

        return values;
    }

    public static Configuration getBuildConfiguration( Cproject cproject, String module, String name ) {

        for (Cconfiguration cconfiguration : getCconfigurations( cproject, module )) {
            Configuration configuration = getBuildConfiguration( cconfiguration );
            if ( name.equals( configuration.getName() ) ) {
                return configuration;
            }
        }

        return null;
    }

    public static Properties getBuildProperties( Configuration configuration ) {

        String buildProperties = configuration.getBuildProperties();

        Properties properties = new Properties();
        for (String entry : buildProperties.split( "," )) {
            String[] property = entry.split( "=" );
            properties.put( property[0], property[1] );
        }
        return properties;
    }
}
