/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.language.swift.internal;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.language.swift.SwiftBundle;

import javax.inject.Inject;

public class DefaultSwiftBundle extends DefaultSwiftBinary implements SwiftBundle {
    private final Provider<RegularFile> informationPropertyList;
    private final DirectoryProperty bundleDir;

    @Inject
    public DefaultSwiftBundle(String name, ProjectLayout projectLayout, ObjectFactory objectFactory, Provider<String> module, boolean debuggable, boolean testable, FileCollection source, ConfigurationContainer configurations, Configuration implementation, DirectoryProperty resourceDirectory) {
        super(name, projectLayout, objectFactory, module, debuggable, testable, source, configurations, implementation);
        this.bundleDir = projectLayout.directoryProperty();
        this.informationPropertyList = resourceDirectory.file("Info.plist");
    }

    @Override
    public Provider<RegularFile> getInformationPropertyList() {
        return informationPropertyList;
    }

    @Override
    public DirectoryProperty getBundleDirectory() {
        return bundleDir;
    }
}
