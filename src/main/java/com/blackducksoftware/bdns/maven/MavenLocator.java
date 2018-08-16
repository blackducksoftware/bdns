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

import java.util.Objects;
import java.util.Optional;

import com.blackducksoftware.bdns.Locator;

/**
 *
 * @author jgustie
 * @see <a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Repository+Layout+-+Final">Repository Layout</a>
 */
public class MavenLocator implements Locator {

    private final MavenRepository repository;

    private final MavenCoordinate identifier;

    MavenLocator(MavenRepository repository, MavenCoordinate identifier) {
        this.repository = Objects.requireNonNull(repository);
        this.identifier = Objects.requireNonNull(identifier);

        if (!identifier.getVersion().isPresent()) {
            throw new IllegalArgumentException("locator requires version: " + identifier);
        }
    }

    @Override
    public MavenRepository context() {
        return repository;
    }

    @Override
    public MavenCoordinate identifier() {
        return identifier;
    }

    @Override
    public String toUriString() {
        if (repository.isDefaultLayout()) {
            StringBuilder url = new StringBuilder();
            url.append(repository.getUrl());
            if (url.length() == 0 || url.charAt(url.length() - 1) != '/') {
                url.append('/');
            }
            for (String s : identifier.getGroupId().split("\\.")) {
                url.append(s).append('/');
            }
            url.append(identifier.getArtifactId()).append('/');
            url.append(identifier.getVersion().get()).append('/');
            url.append(identifier.getArtifactId()).append('-');
            url.append(identifier.getVersion().get());

            identifier.getClassifier().ifPresent(c -> url.append('-').append(c));
            // TODO How do you map packaging to extension without the build extensions?
            url.append('.').append(identifier.getPackaging().orElse("jar"));

            return url.toString();
        } else if (repository.isLegacyLayout()) {
            StringBuilder url = new StringBuilder();
            url.append(repository.getUrl());
            if (url.length() == 0 || url.charAt(url.length() - 1) != '/') {
                url.append('/');
            }
            url.append(identifier.getGroupId());
            // TODO No idea what is allowed for this directory
            url.append(Optional.of("pom").equals(identifier.getPackaging()) ? "/poms/" : "/jars/");
            url.append(identifier.getArtifactId())
                    .append('-').append(identifier.getVersion().get())
                    .append('.').append(identifier.getPackaging().orElse("jar"));

            return url.toString();
        } else {
            throw new UnsupportedOperationException("unsupported layout: " + repository.getLayout());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository, identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenLocator) {
            MavenLocator other = (MavenLocator) obj;
            return repository.equals(other.repository) && identifier.equals(other.identifier);
        }
        return false;
    }

    @Override
    public String toString() {
        if (repository.isDefaultLayout() || repository.isLegacyLayout()) {
            return toUriString();
        } else {
            return repository.getUrl() + "{" + identifier + "}";
        }
    }

}
