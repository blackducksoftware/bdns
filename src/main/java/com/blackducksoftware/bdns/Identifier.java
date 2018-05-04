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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A component identifier. Each namespace will have specific rules about the syntax and decomposition of its
 * identifiers.
 *
 * @implNote Identifiers should have static {@code valueOf(CharSequence)} methods that are expected to be produce an
 *           equivalent object from it's {@code toString} representation. If the identifier decomposition is
 *           sufficiently complex, a builder should also be provided.
 *
 * @author jgustie
 */
public interface Identifier {

    /**
     * Returns the context associated with this identifier. An empty return value indicates the default namespace; for
     * namespace managers which do not support a context, this will always be empty.
     */
    Optional<? extends Context> getContext();

    /**
     * Returns the decomposed representation of this identifier as an unmodifiable list of parts. The resulting list
     * will never be empty.
     */
    default List<String> toList() {
        return Collections.singletonList(toString());
    }

}
