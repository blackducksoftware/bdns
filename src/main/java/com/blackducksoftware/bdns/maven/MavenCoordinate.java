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
import java.util.StringJoiner;
import java.util.regex.Pattern;

import com.blackducksoftware.bdns.Identifier;

/**
 *
 * @author jgustie
 * @see <a href="https://maven.apache.org/pom.html#Maven_Coordinates">Maven Coordinates</a>
 */
public final class MavenCoordinate implements Identifier {

    private static final String DEFAULT_PACKAGING = "jar";

    private final String groupId;

    private final String artifactId;

    private final Optional<MavenVersion> version;

    private final Optional<String> packaging;

    private final Optional<String> classifier;

    private MavenCoordinate(Builder builder) {
        this.groupId = Objects.requireNonNull(builder.groupId);
        this.artifactId = Objects.requireNonNull(builder.artifactId);
        this.version = Optional.of(builder.version);
        this.packaging = Optional.ofNullable(builder.packaging);
        this.classifier = Optional.ofNullable(builder.classifier);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public Optional<MavenVersion> getVersion() {
        return version;
    }

    public Optional<String> getPackaging() {
        return packaging;
    }

    public Optional<String> getClassifier() {
        return classifier;
    }

    @Override
    public MavenCoordinate withoutVersion() {
        return version.isPresent() ? newBuilder().version((MavenVersion) null).build() : this;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version, packaging.orElse(DEFAULT_PACKAGING), classifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenCoordinate) {
            MavenCoordinate other = (MavenCoordinate) obj;
            return groupId.equals(other.groupId)
                    && artifactId.equals(other.artifactId)
                    && version.equals(other.version)
                    && packaging.orElse(DEFAULT_PACKAGING).equals(other.packaging.orElse(DEFAULT_PACKAGING))
                    && classifier.equals(other.classifier);
        }
        return false;
    }

    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(":");
        result.add(groupId);
        result.add(artifactId);
        packaging.ifPresent(result::add);
        classifier.ifPresent(result::add);
        version.map(MavenVersion::toString).ifPresent(result::add);
        return result.toString();
    }

    public static MavenCoordinate valueOf(String value) {
        return parse(value);
    }

    public static MavenCoordinate parse(CharSequence value) {
        String[] parts = Pattern.compile(":").split(value);
        if (parts.length < 3 || parts.length > 5) {
            throw new IllegalArgumentException("invalid Maven identifier: " + value);
        }

        int i = 0;
        Builder builder = new Builder();
        builder.groupId(parts[i++]);
        builder.artifactId(parts[i++]);
        if (parts.length == 4) {
            builder.packaging(parts[i++]);
        }
        if (parts.length == 5) {
            builder.classifier(parts[i++]);
        }
        builder.version(parts[i++]);
        return builder.build();
    }

    public static final class Builder {

        private String groupId;

        private String artifactId;

        private MavenVersion version;

        private String packaging;

        private String classifier;

        public Builder() {
        }

        private Builder(MavenCoordinate mavenIdentifier) {
            this.groupId = mavenIdentifier.groupId;
            this.artifactId = mavenIdentifier.artifactId;
            this.version = mavenIdentifier.version.orElse(null);
            this.packaging = mavenIdentifier.packaging.orElse(null);
            this.classifier = mavenIdentifier.classifier.orElse(null);
        }

        public Builder groupId(CharSequence groupId) {
            this.groupId = Objects.toString(groupId, null);
            return this;
        }

        public Builder artifactId(CharSequence artifactId) {
            this.artifactId = Objects.toString(artifactId, null);
            return this;
        }

        public Builder version(MavenVersion version) {
            this.version = version;
            return this;
        }

        public Builder version(CharSequence version) {
            return version(version != null ? MavenVersion.parse(version) : null);
        }

        public Builder packaging(CharSequence packaging) {
            this.packaging = Objects.toString(packaging, null);
            return this;
        }

        public Builder pomPackaging() {
            return packaging("pom");
        }

        public Builder jarPackaging() {
            return packaging("jar");
        }

        public Builder mavenPluginPackaging() {
            return packaging("maven-plugin");
        }

        public Builder ejbPackaging() {
            return packaging("ejb");
        }

        public Builder warPackaging() {
            return packaging("war");
        }

        public Builder earPackaging() {
            return packaging("ear");
        }

        public Builder rarPackaging() {
            return packaging("rar");
        }

        public Builder parPackaging() {
            return packaging("par");
        }

        public Builder classifier(CharSequence classifier) {
            this.classifier = Objects.toString(classifier, null);
            return this;
        }

        /**
         * Conditionally set the packaging and classifier. The packaging will only be set if the classifier is not
         * {@code null} or it is not the default packaging.
         */
        public Builder packagingIfNeeded(CharSequence packaging, CharSequence classifier) {
            if (classifier != null) {
                return packaging(packaging).classifier(classifier);
            } else if (!Objects.equals(packaging, DEFAULT_PACKAGING)) {
                return packaging(packaging);
            } else {
                return this;
            }
        }

        public MavenCoordinate build() {
            if (classifier != null && packaging == null) {
                throw new IllegalStateException("packaging is required when using classifier");
            }
            return new MavenCoordinate(this);
        }
    }

}
