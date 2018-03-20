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
     * Returns the mapping of namespace types to their corresponding manager.
     */
    private static Map<String, NamespaceManager> registeredNamespaceManagers() {
        return StreamSupport.stream(() -> ServiceLoader.load(NamespaceManager.class).spliterator(), NONNULL, false)
                .collect(toMap(NamespaceManager::type, m -> m));
    }

    /**
     * Returns a namespace manager by it type; throwing an {@code IllegalArgumentException} if the type is not
     * recognized.
     */
    public static NamespaceManager get(String value) {
        return Optional.ofNullable(NAMESPACE_MANAGERS.get(toLowerCase(value)))
                .orElseThrow(() -> new IllegalArgumentException("unrecognized namespace: " + value));
    }

    /**
     * Attempts a lookup and cast of a namespace manager in a single operation.
     *
     * @implNote This only works because visible no-argument constructor shares it's underlying implementation.
     */
    public static <M extends NamespaceManager> M get(Class<M> type) {
        return type.cast(get(namespaceManagerType(type)));
    }

    /**
     * Computes the namespace manager type based on it's implementation class.
     */
    private static String namespaceManagerType(Class<? extends NamespaceManager> type) {
        return type.getSimpleName();
    }

    /**
     * Combines namespace lookup and identifier lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).identifier(value)}.
     */
    public static Identifier identifier(String namespace, Object value) {
        return get(namespace).identifier(value);
    }

    /**
     * Combines namespace lookup and context lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).context(value)}.
     */
    public static Context context(String namespace, Object value) {
        return get(namespace).context(value);
    }

    /**
     * Combines namespace lookup and default context lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).defaultContext()}.
     */
    public static Context defaultContext(String namespace) {
        return get(namespace).defaultContext();
    }

    /**
     * Combines namespace lookup and scope lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).scope(value)}.
     */
    public static Scope scope(String namespace, Object value) {
        return get(namespace).scope(value);
    }

    /**
     * Combines namespace lookup and version lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).version(value)}.
     */
    public static Version version(String namespace, Object value) {
        return get(namespace).version(value);
    }

    /**
     * Combines namespace lookup and version range lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).versionRange(value)}.
     */
    public static VersionRange versionRange(String namespace, Object value) {
        return get(namespace).versionRange(value);
    }

    /**
     * Combines namespace lookup and package URL lookup in a single operation.
     *
     * @implSpec Same as {@code get(namespace).packageUrl(value)}.
     */
    public static String packageUrl(String namespace, Object identifier, Object context) {
        return get(namespace).packageUrl(identifier, context);
    }

    /**
     * The namespace manager type.
     */
    private final String type;

    /**
     * Only visible for special namespace managers in this package.
     */
    NamespaceManager(String type) {
        this.type = toLowerCase(type);
    }

    protected NamespaceManager() {
        this.type = toLowerCase(namespaceManagerType(getClass()));
    }

    /**
     * Returns this namespace manager's type.
     */
    public final String type() {
        return type;
    }

    // TODO Have things like "display name", "description", "URL list", etc.

    // @apiNote Accepts an `Object` to allow for the possibility of cross namespace conversion or assisted type casts.

    public abstract Identifier identifier(Object value);

    public abstract Context context(Object value);

    public abstract Context defaultContext();

    public abstract Scope scope(Object value);

    public abstract Version version(Object value);

    public abstract VersionRange versionRange(Object value);

    // TODO Does this just belong on the context? It does need the `type()` value from the manager...
    public abstract String packageUrl(Object identifier, Object context);
    // TODO What about parsing, e.g. `void fromPackageUrl(String purl, BiConsumer<Identifier, Context> action)`?

    /**
     * Returns the supplied character sequence with all ASCII upper case characters replaced with their lower case
     * equivalents. Generally more efficient then {@code String.toLowerCase} as it only considers a small range of
     * characters to be eligible for case changes.
     */
    protected static String toLowerCase(CharSequence value) {
        int i = 0, len = value.length();
        char[] result;
        if (value instanceof String) {
            // Optimize the String path so we can just return the input if it is already lower case
            for (; i < len; ++i) {
                char c = value.charAt(i);
                if ((c >= 'A') && (c <= 'Z')) {
                    break;
                }
            }
            if (i == len) {
                return (String) value;
            } else {
                result = ((String) value).toCharArray();
            }
        } else {
            result = new char[value.length()];
        }

        // At this point we are working with a copy
        for (; i < len; ++i) {
            char c = value.charAt(i);
            result[i] = (c >= 'A') && (c <= 'Z') ? (char) (c ^ 0x20) : c;
        }
        return String.valueOf(result);
    }

    // TODO Percent encoding method(s)

}
