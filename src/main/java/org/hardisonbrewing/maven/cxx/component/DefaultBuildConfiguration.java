package org.hardisonbrewing.maven.cxx.component;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.hardisonbrewing.maven.core.FileUtils;
import org.hardisonbrewing.maven.cxx.ProjectService;

public class DefaultBuildConfiguration implements BuildConfiguration {

    /**
     * @parameter
     */
    public String sourceDirectory;

    public String getSourceDirectory() {

        MavenProject project = ProjectService.getProject();
        String userDefined = project.getOriginalModel().getBuild().getSourceDirectory();

        if ( userDefined == null && sourceDirectory == null ) {
            return project.getBuild().getSourceDirectory();
        }

        String _sourceDirectory = userDefined == null ? sourceDirectory : userDefined;
        _sourceDirectory = FileUtils.normalize( _sourceDirectory );

        StringBuffer sourceDirectoryPath = new StringBuffer();

        if ( !_sourceDirectory.startsWith( File.separator ) ) {
            sourceDirectoryPath.append( ProjectService.getBaseDirPath() );
            sourceDirectoryPath.append( File.separator );
        }
        sourceDirectoryPath.append( FileUtils.trimEndSeperator( _sourceDirectory ) );
        return sourceDirectoryPath.toString();
    }
}
