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

import java.util.Optional;

/**
 * A component identifier. Identifiers are usually associated with the creation of software project and not the usage
 * from another project; for example an identifier would correspond to the GAV defined at the top level of a Maven POM
 * (and <em>not</em> the GAV from the dependencies section).
 * <p>
 * Each namespace will have specific rules about the syntax of its identifiers; namespace managers need only provide
 * minimal decomposition (e.g. extraction of the version portion of the identifier). Individual namespace specific
 * implementations may provide more detailed identifier decomposition.
 *
 * @implNote Identifiers should have static {@code valueOf(CharSequence)} methods that are expected to be produce an
 *           equivalent object from it's {@code toString} representation. If the identifier decomposition is
 *           sufficiently complex, a builder should also be provided.
 *
 * @author jgustie
 */
public interface Identifier {

    /**
     * Extracts the version portion of this identifier. The result may be empty if this identifier does not contain any
     * version information or if the namespace itself does not distinguish between versions.
     *
     * @return the version, if any, associated with this identifier
     */
    Optional<? extends Version> getVersion();

}
