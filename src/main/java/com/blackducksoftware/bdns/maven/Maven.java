/*
 * Copyright 2018 Synopsys, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackducksoftware.bdns.maven;

import com.blackducksoftware.bdns.NamespaceManager;

/**
 * The Maven namespace manager.
 *
 * @author jgustie
 */
public class Maven extends NamespaceManager {

    /**
     * Returns the current Maven namespace manager.
     */
    public static Maven get() {
        return get(Maven.class);
    }

    /**
     * Constructor for reflective instantiation only.
     *
     * @deprecated Use {@link Maven#get()} instead.
     */
    @Deprecated
    public Maven() {
    }

    @Override
    public MavenRepository context(CharSequence context) {
        return MavenRepository.parse(context);
    }

    @Override
    public MavenCoordinate identifier(CharSequence identifier) {
        return MavenCoordinate.parse(identifier);
    }

    @Override
    public MavenVersion version(CharSequence version) {
        return MavenVersion.parse(version);
    }

    @Override
    public MavenVersionRequirement versionRange(CharSequence versionRange) {
        return MavenVersionRequirement.parse(versionRange);
    }

    @Override
    public MavenScope scope(CharSequence scope) {
        return MavenScope.parse(scope);
    }

}
