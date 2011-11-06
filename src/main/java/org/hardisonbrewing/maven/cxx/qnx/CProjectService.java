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

import generated.org.eclipse.cdt.Cproject;
import generated.org.eclipse.cdt.StorageModule;
import generated.org.eclipse.cdt.StorageModule.Cconfiguration;
import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

public class CProjectService {

    public static final String MODULE_SETTINGS = "org.eclipse.cdt.core.settings";

    public static final String STORAGE_MODULE_ID = "moduleId";
    public static final String CDT_BUILD_SYSTEM = "cdtBuildSystem";

    public static final String CONFIG_ID = "id";
    public static final String CONFIG_NAME = "name";
    public static final String CONFIG_PARENT = "parent";
    public static final String CONFIG_BUILD_PROPERTIES = "buildProperties";
    public static final String CONFIG_ARTIFACT_NAME = "artifactName";
    public static final String CONFIG_BUILD_ARTIFACT_TYPE = "buildArtefactType";

    public static final String BUILD_TYPE = "org.eclipse.cdt.build.core.buildType";
    public static final String BUILD_TYPE_RELEASE = "org.eclipse.cdt.build.core.buildType.release";
    public static final String BUILD_TYPE_DEBUG = "org.eclipse.cdt.build.core.buildType.debug";

    public static final String BUILD_ARTIFACT_TYPE = "org.eclipse.cdt.build.core.buildArtefactType";
    public static final String BUILD_ARTIFACT_TYPE_EXE = "org.eclipse.cdt.build.core.buildArtefactType.exe";

    public static Cproject readCProject( File file ) {

        return JAXB.unmarshal( file, Cproject.class );
    }

    public static List<Cconfiguration> getCconfigurations( Cproject cproject, String module ) {

        for (StorageModule storageModule : cproject.getStorageModule()) {
            Map<QName, String> attributes = storageModule.getOtherAttributes();
            if ( module.equals( attributes.get( QName.valueOf( STORAGE_MODULE_ID ) ) ) ) {
                return storageModule.getCconfiguration();
            }
        }

        return null;
    }

    public static Configuration getBuildConfiguration( Cconfiguration cconfiguration ) {

        for (StorageModule storageModule : cconfiguration.getStorageModule()) {
            Map<QName, String> attributes = storageModule.getOtherAttributes();
            if ( CDT_BUILD_SYSTEM.equals( attributes.get( QName.valueOf( STORAGE_MODULE_ID ) ) ) ) {
                return storageModule.getConfiguration();
            }
        }

        return null;
    }

    public static Configuration getBuildConfiguration( Cproject cproject, String module, String name ) {

        for (Cconfiguration cconfiguration : getCconfigurations( cproject, module )) {
            Configuration configuration = getBuildConfiguration( cconfiguration );
            Map<QName, String> attributes = configuration.getOtherAttributes();
            if ( name.equals( attributes.get( QName.valueOf( CONFIG_NAME ) ) ) ) {
                return configuration;
            }
        }

        return null;
    }
}
