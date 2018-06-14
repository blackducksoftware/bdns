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

import static java.util.Collections.unmodifiableMap;
import static java.util.Spliterator.NONNULL;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * Primary interface for the namespacing system. Defines the both the contract and repository for each namespace.
 * Namespace managers should be registered using the {@linkplain ServiceLoader service discovery} mechanism.
 *
 * @author jgustie
 */
public abstract class NamespaceManager {

    /**
     * Namespace manager registry, computed once at initialization.
     */
    private static final Map<String, NamespaceManager> NAMESPACE_MANAGERS;
    static {
        Map<String, NamespaceManager> namespaceManagers = new HashMap<>();
        namespaceManagers.putAll(registeredNamespaceManagers());
        NAMESPACE_MANAGERS = unmodifiableMap(namespaceManagers);
    }

    /**
     * Returns the mapping of namespaces to their corresponding manager.
     */
    private static Map<String, NamespaceManager> registeredNamespaceManagers() {
        return StreamSupport.stream(() -> ServiceLoader.load(NamespaceManager.class).spliterator(), NONNULL, false)
                .collect(toMap(NamespaceManager::namespace, m -> m));
    }

    /**
     * Lookup a namespace manager.
     *
     * @throws IllegalArgumentException
     *             if there is no manager registered with the specified namespace.
     */
    public static NamespaceManager get(String namespace) {
        return Optional.ofNullable(NAMESPACE_MANAGERS.get(namespace.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("unrecognized namespace: " + namespace));
    }

    /**
     * Attempts a lookup and cast of a namespace manager in a single operation. If the namespace manager's type is
     * known, this method may be preferable to {@code String} variant as it will expose a type safe manager.
     *
     * @implNote This only works because visible no-argument constructor shares it's underlying implementation.
     */
    public static <M extends NamespaceManager> M get(Class<M> type) {
        return type.cast(get(type.getSimpleName()));
    }

    /**
     * The namespace for this manager.
     */
    private final String namespace;

    /**
     * Only visible for special namespace managers whose implementation is generic for multiple types.
     */
    NamespaceManager(String namespace) {
        this.namespace = namespace.toLowerCase();
    }

    protected NamespaceManager() {
        this.namespace = getClass().getSimpleName().toLowerCase();
    }

    /**
     * Returns this namespace manager's type.
     */
    public final String namespace() {
        // By convention this will be the lower case simple name of the manager
        return namespace;
    }

    // TODO Have things like "display name", "description", "URL list", etc.

    /**
     * Creates a new context.
     *
     * @param context
     *            the context to be parsed
     * @return the namespace specific representation of the context
     * @throws NullPointerException
     *             if the supplied context is {@code null}
     * @throws IllegalArgumentException
     *             if the supplied context cannot be parsed
     */
    public abstract Context context(CharSequence context);

    /**
     * Creates a new identifier in the default context.
     *
     * @param identifier
     *            the identifier to be parsed
     * @return the namespace specific representation of the identifier
     * @throws NullPointerException
     *             if the supplied identifier is {@code null}
     * @throws IllegalArgumentException
     *             if the supplied identifier cannot be parsed
     */
    public abstract Identifier identifier(CharSequence identifier);

    /**
     * Creates a new version.
     *
     * @param version
     *            the version to be parsed
     * @return the namespace specific representation of the version
     * @throws NullPointerException
     *             if the supplied version is {@code null}
     * @throws IllegalArgumentException
     *             if the supplied version cannot be parsed
     */
    public abstract Version version(CharSequence version);

    /**
     * Creates a new version range.
     *
     * @param versionRange
     *            the version range to be parsed
     * @return the namespace specific representation of the version range
     * @throws NullPointerException
     *             if the supplied version range is {@code null}
     * @throws IllegalArgumentException
     *             if the supplied version range cannot be parsed
     */
    public abstract VersionRange versionRange(CharSequence versionRange);

    /**
     * Creates a new scope.
     *
     * @param scope
     *            the scope to be parsed
     * @return the namespace specific representation of the scope
     * @throws NullPointerException
     *             if the supplied scope is {@code null}
     * @throws IllegalArgumentException
     *             if the supplied scope cannot be parsed
     */
    public abstract Scope scope(CharSequence scope);

}
