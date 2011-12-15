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

import generated.net.rim.bar.BarDescriptor;

import java.io.File;
import java.io.IOException;

import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.core.JoJoMojoImpl;
import org.hardisonbrewing.maven.cxx.qde.model.AssetResource;

/**
 * @goal qde-generate-resources
 * @phase qde-generate-resources
 */
public class GenerateResourcesMojo extends JoJoMojoImpl {

    /**
     * @parameter
     */
    public String target;

    @Override
    public void execute() {

        copyBarDescriptor();

        BarDescriptor barDescriptor = BarDescriptorService.getBarDescriptor();
        AssetResource[] assetResources = BarDescriptorService.getAssetResources( barDescriptor, target );

        if ( assetResources != null ) {

            for (AssetResource assetResource : assetResources) {

                String srcFilePath = assetResource.getSrcFilePath();
                String destFilePath = assetResource.getDestFilePath();

                try {
                    FileUtils.copyFile( srcFilePath, destFilePath );
                }
                catch (IOException e) {
                    throw new IllegalStateException( e.getMessage(), e );
                }
            }
        }
    }

    private void copyBarDescriptor() {

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        File file = BarDescriptorService.getBarDescriptorFile();
        String filePath = file.getPath();
        String filePathPrefix = file.getParent();

        org.hardisonbrewing.maven.cxx.generic.GenerateSourcesMojo.copyFile( filePath, filePathPrefix, targetDirectoryPath );
    }
}
