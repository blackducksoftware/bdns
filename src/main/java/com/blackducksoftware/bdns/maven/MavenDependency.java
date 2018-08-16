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

import com.blackducksoftware.bdns.Dependency;
import com.blackducksoftware.bdns.Version;

/**
 *
 * @author jgustie
 * @see <a href="https://maven.apache.org/pom.html#Dependencies">Dependencies</a>
 */
public class MavenDependency implements Dependency {

    private static final String DEFAULT_TYPE = "jar";

    private static final MavenScope DEFAULT_SCOPE = MavenScope.compile;

    private static final Boolean DEFAULT_OPTIONAL = Boolean.FALSE;

    private final String groupId;

    private final String artifactId;

    private final MavenVersionRequirement version;

    private final Optional<String> classifier;

    private final Optional<String> type;

    private final Optional<MavenScope> scope;

    private final Optional<String> systemPath;

    private final Optional<Boolean> optional;

    private MavenDependency(Builder builder) {
        this.groupId = Objects.requireNonNull(builder.groupId);
        this.artifactId = Objects.requireNonNull(builder.artifactId);
        this.version = Objects.requireNonNull(builder.version);
        this.classifier = Optional.ofNullable(builder.classifier);
        this.type = Optional.ofNullable(builder.type);
        this.scope = Optional.ofNullable(builder.scope);
        this.systemPath = Optional.ofNullable(builder.systemPath);
        this.optional = Optional.ofNullable(builder.optional);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public MavenVersionRequirement getVersionRange() {
        return version;
    }

    public Optional<String> getClassifier() {
        return classifier;
    }

    public Optional<String> getType() {
        return type;
    }

    @Override
    public Optional<MavenScope> getScope() {
        return scope;
    }

    public Optional<String> getSystemPath() {
        return systemPath;
    }

    public Optional<Boolean> getOptional() {
        return optional;
    }

    @Override
    public MavenCoordinate resolve(Version version) {
        if (version instanceof MavenVersion && getVersionRange().test(version)) {
            MavenCoordinate.Builder result = new MavenCoordinate.Builder()
                    .groupId(groupId)
                    .artifactId(artifactId)
                    .version((MavenVersion) version);
            classifier.ifPresent(result::classifier);
            type.ifPresent(result::packaging); // TODO Doesn't this need conversion?
            return result.build();
        }
        return null;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId,
                artifactId,
                version,
                classifier,
                type.orElse(DEFAULT_TYPE),
                scope.orElse(DEFAULT_SCOPE),
                systemPath,
                optional.orElse(DEFAULT_OPTIONAL));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenDependency) {
            MavenDependency o = (MavenDependency) obj;
            return groupId.equals(o.groupId)
                    && artifactId.equals(o.artifactId)
                    && version.equals(o.version)
                    && classifier.equals(o.classifier)
                    && type.orElse(DEFAULT_TYPE).equals(o.type.orElse(DEFAULT_TYPE))
                    && scope.orElse(DEFAULT_SCOPE).equals(o.scope.orElse(DEFAULT_SCOPE))
                    && systemPath.equals(o.systemPath)
                    && optional.orElse(DEFAULT_OPTIONAL).equals(o.optional.orElse(DEFAULT_OPTIONAL));
        }
        return false;
    }

    @Override
    public String toString() {
        // The closest thing to a string representation is the serialized XML
        StringBuilder result = new StringBuilder();
        result.append("    <dependency>");
        result.append("<groupId>").append(groupId).append("</groupId>");
        result.append("<artifactId>").append(artifactId).append("</artifactId>");
        result.append("<version>").append(version).append("</version>");
        classifier.ifPresent(c -> result.append("<classifier>").append(c).append("</classifier>"));
        type.ifPresent(t -> result.append("<type>").append(t).append("</type>"));
        scope.ifPresent(s -> result.append("<scope>").append(s).append("</scope>"));
        systemPath.ifPresent(s -> result.append("<systemPath>").append(s).append("</systemPath>"));
        optional.ifPresent(o -> result.append("<optional>").append(o).append("</optional>"));
        result.append("</dependency>");
        return result.toString();
    }

    public static final class Builder {

        private String groupId;

        private String artifactId;

        private MavenVersionRequirement version;

        private String classifier;

        private String type;

        private MavenScope scope;

        private String systemPath;

        private Boolean optional;

        public Builder() {
        }

        private Builder(MavenDependency mavenDependency) {
            this.groupId = mavenDependency.groupId;
            this.artifactId = mavenDependency.artifactId;
            this.version = mavenDependency.version;
            this.classifier = mavenDependency.classifier.orElse(null);
            this.type = mavenDependency.type.orElse(null);
            this.scope = mavenDependency.scope.orElse(null);
            this.systemPath = mavenDependency.systemPath.orElse(null);
            this.optional = mavenDependency.optional.orElse(null);
        }

        public Builder groupId(CharSequence groupId) {
            this.groupId = Objects.toString(groupId, null);
            return this;
        }

        public Builder artifactId(CharSequence artifactId) {
            this.artifactId = Objects.toString(artifactId, null);
            return this;
        }

        public Builder version(MavenVersionRequirement version) {
            this.version = version;
            return this;
        }

        public Builder version(CharSequence version) {
            return version(version != null ? MavenVersionRequirement.parse(version) : null);
        }

        public Builder classifier(CharSequence classifier) {
            this.classifier = Objects.toString(classifier, null);
            return this;
        }

        public Builder type(CharSequence type) {
            this.type = Objects.toString(type, null);
            return this;
        }

        public Builder scope(MavenScope scope) {
            this.scope = scope;
            return this;
        }

        public Builder scope(CharSequence scope) {
            return scope(scope != null ? MavenScope.valueOf(scope.toString()) : null);
        }

        public Builder systemPath(CharSequence systemPath) {
            this.systemPath = Objects.toString(systemPath, null);
            return this;
        }

        public Builder optional(Boolean optional) {
            this.optional = optional;
            return this;
        }

        public MavenDependency build() {
            if (systemPath != null && scope != MavenScope.system) {
                throw new IllegalStateException("system path requires scope of system");
            }
            return new MavenDependency(this);
        }
    }

}
