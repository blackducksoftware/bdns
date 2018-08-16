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
package com.blackducksoftware.bdns;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The reserved namespace manager implementation is used for namespace types that are recognized but not yet supported.
 *
 * @author jgustie
 */
public class ReservedNamespaceManager extends NamespaceManager {

    /**
     * Generates a namespace manager for each of the reserved tokens.
     */
    static Map<String, ReservedNamespaceManager> reservedNamespaceManagers() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(NamespaceManager.class.getResourceAsStream("reserved"), UTF_8))) {
            return reader.lines()
                    .filter(t -> !t.isEmpty() && !t.startsWith("#"))
                    .distinct()
                    .map(ReservedNamespaceManager::new)
                    .collect(Collectors.toMap(NamespaceManager::namespace, m -> m));
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * Base class for reserved namespace implementations.
     */
    private static abstract class ReservedNamespaceObject {

        /**
         * The namespace this instance was created under.
         */
        private final String namespace;

        /**
         * The raw value used to create this instance.
         */
        private final String value;

        protected ReservedNamespaceObject(String namespace, CharSequence value) {
            this.namespace = Objects.requireNonNull(namespace);
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ReservedNamespaceObject) {
                ReservedNamespaceObject other = (ReservedNamespaceObject) obj;
                return namespace.equals(other.namespace) && value.equals(other.value);
            }
            return false;
        }
    }

    private static class ReservedNamespaceContext extends ReservedNamespaceObject implements Context {
        private ReservedNamespaceContext(String namespace, CharSequence context) {
            super(namespace, context);
        }

        @Override
        public Locator locate(Identifier identifier) {
            throw new UnsupportedOperationException("cannot resolve locator for reserved namespace");
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReservedNamespaceContext && super.equals(obj);
        }
    }

    private static class ReservedNamespaceIdentifier extends ReservedNamespaceObject implements Identifier {
        private ReservedNamespaceIdentifier(String namespace, CharSequence identifier) {
            super(namespace, identifier);
        }

        @Override
        public Optional<? extends Version> getVersion() {
            return Optional.empty();
        }

        @Override
        public ReservedNamespaceIdentifier withoutVersion() {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReservedNamespaceIdentifier && super.equals(obj);
        }
    }

    private static class ReservedNamespaceVersion extends ReservedNamespaceObject implements Version {
        private ReservedNamespaceVersion(String namespace, CharSequence version) {
            super(namespace, version);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReservedNamespaceVersion && super.equals(obj);
        }
    }

    private static class ReservedNamespaceVersionRange extends ReservedNamespaceObject implements VersionRange {
        private ReservedNamespaceVersionRange(String namespace, CharSequence versionRange) {
            super(namespace, versionRange);
        }

        @Override
        public boolean test(Version t) {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReservedNamespaceVersionRange && super.equals(obj);
        }
    }

    private static class ReservedNamespaceScope extends ReservedNamespaceObject implements Scope {
        private ReservedNamespaceScope(String namespace, CharSequence scope) {
            super(namespace, scope);
        }

        @Override
        public String name() {
            return toString();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReservedNamespaceScope && super.equals(obj);
        }
    }

    private ReservedNamespaceManager(String type) {
        super(type);
    }

    @Override
    public Context context(CharSequence context) {
        return new ReservedNamespaceContext(namespace(), context);
    }

    @Override
    public Identifier identifier(CharSequence identifier) {
        return new ReservedNamespaceIdentifier(namespace(), identifier);
    }

    @Override
    public Version version(CharSequence version) {
        return new ReservedNamespaceVersion(namespace(), version);
    }

    @Override
    public VersionRange versionRange(CharSequence versionRange) {
        return new ReservedNamespaceVersionRange(namespace(), versionRange);
    }

    @Override
    public Scope scope(CharSequence scope) {
        return new ReservedNamespaceScope(namespace(), scope);
    }

}
