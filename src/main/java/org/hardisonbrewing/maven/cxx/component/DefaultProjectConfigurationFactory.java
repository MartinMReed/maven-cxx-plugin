package org.hardisonbrewing.maven.cxx.component;

import org.hardisonbrewing.maven.core.model.ProjectConfiguration;

public class DefaultProjectConfigurationFactory implements ProjectConfigurationFactory {

    @Override
    public ProjectConfiguration createProjectConfiguration() {

        return new ProjectConfiguration();
    }
}
