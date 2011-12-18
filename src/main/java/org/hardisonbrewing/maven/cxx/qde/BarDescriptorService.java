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
package org.hardisonbrewing.maven.cxx.qde;

import generated.net.rim.bar.Asset;
import generated.net.rim.bar.AssetConfiguration;
import generated.net.rim.bar.Qnx;
import generated.org.eclipse.cdt.StorageModule.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hardisonbrewing.jaxb.JAXB;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojo;
import org.hardisonbrewing.maven.cxx.ProjectService;
import org.hardisonbrewing.maven.cxx.qde.model.AssetResource;

public final class BarDescriptorService {

    public static final String BAR_DESCRIPTOR_FILENAME = "bar-descriptor.xml";

    private static Qnx barDescriptor;

    private BarDescriptorService() {

        // do nothing
    }

    public static Qnx readBarDescriptor( File file ) {

        if ( !file.exists() ) {
            JoJoMojo.getMojo().getLog().error( "Unable to locate application descriptor file: " + file );
            throw new IllegalStateException();
        }

        try {
            return JAXB.unmarshal( file, Qnx.class );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to unmarshal bar-descriptor file: " + file );
            throw new IllegalStateException( e );
        }
    }

    public static void writeBarDescriptor( Qnx barDescriptor, File file ) {

        try {
            JAXB.marshal( file, barDescriptor );
        }
        catch (JAXBException e) {
            JoJoMojo.getMojo().getLog().error( "Unable to marshal bar-descriptor file: " + file );
            throw new IllegalStateException( e );
        }
    }

    public static final AssetResource[] getAssetResources( Qnx barDescriptor, String target ) {

        List<AssetResource> assetResources = new ArrayList<AssetResource>();

        getResources( barDescriptor.getAsset(), assetResources );

        Configuration configuration = CProjectService.getBuildConfiguration( target );
        String configurationId = configuration.getId();

        List<AssetConfiguration> assetConfigurations = barDescriptor.getConfiguration();
        if ( assetConfigurations != null ) {
            for (AssetConfiguration assetConfiguration : assetConfigurations) {
                if ( configurationId.equals( assetConfiguration.getId() ) ) {
                    getResources( assetConfiguration.getAsset(), assetResources );
                    break;
                }
            }
        }

        if ( assetResources.isEmpty() ) {
            return null;
        }

        AssetResource[] _assetResources = new AssetResource[assetResources.size()];
        assetResources.toArray( _assetResources );
        return _assetResources;
    }

    private static final void getResources( List<Asset> assets, List<AssetResource> assetResources ) {

        if ( assets == null ) {
            return;
        }

        for (Asset asset : assets) {
            AssetResource assetResource = getAssetResource( asset );
            assetResources.add( assetResource );
        }
    }

    public static final AssetResource getAssetResource( Asset asset ) {

        String filePath = asset.getPath();
        filePath = PropertiesService.populateTemplateVariables( filePath, "${", "}" );

        if ( FileUtils.isCanonical( filePath ) ) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append( ProjectService.getBaseDirPath() );
            stringBuffer.append( File.separator );
            stringBuffer.append( filePath );
            filePath = stringBuffer.toString();
        }

        StringBuffer targetPathBuffer = new StringBuffer();
        targetPathBuffer.append( TargetDirectoryService.getGeneratedResourcesDirectoryPath() );
        targetPathBuffer.append( File.separator );
        targetPathBuffer.append( asset.getValue() );
        String targetPath = targetPathBuffer.toString();

        AssetResource assetResource = new AssetResource();
        assetResource.setAsset( asset );
        assetResource.setSrcFilePath( filePath );
        assetResource.setDestFilePath( targetPath );
        return assetResource;
    }

    public static String getBarDescriptorPath() {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( BAR_DESCRIPTOR_FILENAME );
        return stringBuffer.toString();
    }

    public static File getBarDescriptorFile() {

        return new File( getBarDescriptorPath() );
    }

    public static final String getBarPath( Qnx barDescriptor ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( TargetDirectoryService.getTargetDirectoryPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( barDescriptor.getName() );
        stringBuffer.append( ".bar" );
        return stringBuffer.toString();
    }

    public static Qnx getBarDescriptor() {

        return barDescriptor;
    }

    public static void loadBarDescriptor() {

        barDescriptor = readBarDescriptor( getBarDescriptorFile() );
    }
}
