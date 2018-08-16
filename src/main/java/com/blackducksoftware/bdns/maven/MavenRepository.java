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
import java.util.regex.Pattern;

import com.blackducksoftware.bdns.Context;
import com.blackducksoftware.bdns.Identifier;

/**
 *
 * @author jgustie
 * @see <a href=
 *      "https://maven.apache.org/plugins/maven-dependency-plugin/get-mojo.html#remoteRepositories">dependency:get</a>
 */
public class MavenRepository implements Context {

    /**
     * Constant for the Central Repository populated using current values from the Super POM.
     *
     * @see <a href="https://maven.apache.org/ref/current/maven-model-builder/super-pom.html">Super POM</a>
     */
    private static final MavenRepository MAVEN_CENTRAL = new Builder()
            .id("central")
            .name("Central Repository")
            .url("http://repo.maven.apache.org/maven2")
            .build();

    /**
     * Constant for the JCenter repository populated using suggested values from JFrog.
     *
     * @see <a href="https://bintray.com/bintray/jcenter">Set me up!</a>
     */
    private static final MavenRepository JCENTER = new Builder()
            .id("central")
            .name("bintray")
            .url("https://jcenter.bintray.com")
            .build();

    /**
     * Returns the canonical context for Maven Central.
     */
    public static MavenRepository mavenCentral() {
        return MAVEN_CENTRAL;
    }

    /**
     * Returns the canonical context for JCenter.
     */
    public static MavenRepository jcenter() {
        return JCENTER;
    }

    private final String id;

    private final String name;

    private final String url;

    private final String layout;

    private MavenRepository(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.url = Objects.requireNonNull(builder.url, "url");
        this.layout = builder.layout;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLayout() {
        return layout;
    }

    public boolean isDefaultLayout() {
        return layout == null || layout.equals("default");
    }

    public boolean isLegacyLayout() {
        return Objects.equals(layout, "legacy");
    }

    @Override
    public MavenLocator locate(Identifier identifier) {
        if (identifier instanceof MavenCoordinate) {
            return new MavenLocator(this, (MavenCoordinate) identifier);
        } else {
            throw new IllegalArgumentException("incorrect namespace");
        }
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenRepository) {
            return url.equals(((MavenRepository) obj).url);
        }
        return false;
    }

    @Override
    public String toString() {
        if (id != null) {
            return id + "::" + Objects.toString(layout, "") + "::" + url;
        } else {
            return url;
        }
    }

    public static MavenRepository valueOf(String input) {
        return parse(input);
    }

    public static MavenRepository parse(CharSequence input) {
        String[] parts = Pattern.compile("::").split(input);
        if (parts.length != 1 && parts.length != 3) {
            throw new IllegalArgumentException("invalid Maven context: " + input);
        }

        Builder builder = new Builder();
        if (parts.length == 1) {
            builder.url(parts[0]);
        } else if (parts.length == 3) {
            builder.id(parts[0]).layout(parts[1]).url(parts[2]);
        }
        return builder.build();
    }

    public static final class Builder {

        private String id;

        private String name;

        private String url;

        private String layout;

        public Builder() {
        }

        private Builder(MavenRepository mavenRepository) {
            this.id = mavenRepository.id;
            this.name = mavenRepository.name;
            this.url = mavenRepository.url;
            this.layout = mavenRepository.layout;
        }

        public Builder id(CharSequence id) {
            this.id = Objects.toString(id, null);
            return this;
        }

        public Builder name(CharSequence name) {
            this.name = Objects.toString(name, null);
            return this;
        }

        public Builder url(CharSequence url) {
            this.url = Objects.toString(url, null);
            return this;
        }

        public Builder layout(CharSequence layout) {
            this.layout = Optional.ofNullable(layout).map(Object::toString).filter(s -> !s.isEmpty()).orElse(null);
            return this;
        }

        public Builder defaultLayout() {
            return layout("default");
        }

        public Builder legacyLayout() {
            return layout("legacy");
        }

        public MavenRepository build() {
            return new MavenRepository(this);
        }
    }

}
