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
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Alias namespace managers allow the functionality of one namespace to be accessed via another. This may be necessary
 * when, for example a namespace is renamed or is commonly accessed via another name.
 *
 * @author jgustie
 */
public class AliasNamespaceManager extends NamespaceManager {

    /**
     * Generates a namespace manager for each of the registered aliases.
     */
    static Map<String, AliasNamespaceManager> aliasNamespaceManagers() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(NamespaceManager.class.getResourceAsStream("aliases"), UTF_8))) {
            return reader.lines()
                    .filter(t -> !t.isEmpty() && !t.startsWith("#"))
                    .distinct()
                    .flatMap(a -> {
                        String[] parts = a.split("\\s+");
                        String namespace = parts[parts.length - 1];
                        return Arrays.stream(parts, 0, parts.length - 1).map(alias -> new AliasNamespaceManager(alias, namespace));
                    })
                    .collect(toMap(NamespaceManager::namespace, m -> m));
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * The namespace that we are aliasing (i.e. the "real" namespace).
     */
    private final String namespace;

    private AliasNamespaceManager(String alias, String namespace) {
        super(alias);
        this.namespace = Objects.requireNonNull(namespace);
    }

    /**
     * Returns the delegate namespace manager.
     */
    private NamespaceManager delegate() {
        return NamespaceManager.get(namespace);
    }

    /**
     * Returns the type being aliased.
     */
    public final String aliasOf() {
        return namespace;
    }

    @Override
    public Context context(CharSequence context) {
        return delegate().context(context);
    }

    @Override
    public Identifier identifier(CharSequence identifier) {
        return delegate().identifier(identifier);
    }

    @Override
    public Version version(CharSequence version) {
        return delegate().version(version);
    }

    @Override
    public VersionRange versionRange(CharSequence versionRange) {
        return delegate().versionRange(versionRange);
    }

    @Override
    public Scope scope(CharSequence scope) {
        return delegate().scope(scope);
    }

}
