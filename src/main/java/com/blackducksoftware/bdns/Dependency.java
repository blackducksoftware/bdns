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
 * A dependency to another component. Dependencies are similar to identifiers in that they are used to identify a
 * requirement to another software project, however dependencies may carry additional information used by the namespace
 * specific resolver.
 *
 * @implNote Namespace managers which do not distinguish between identifiers and dependencies are encouraged to
 *           implement both interfaces from a single implementation.
 *
 * @author jgustie
 */
public interface Dependency {

    /**
     * Extracts the version range portion of this dependency.
     *
     * @implNote if this dependency is also an identifier, the resulting range must be satisfied by any non-empty
     *           version returned as part of the identifier decomposition
     *
     * @return the range of versions which can satisfy this dependency requirement
     */
    VersionRange getVersionRange();

    /**
     * Extracts the scope portion of this dependency.
     *
     * @return the scope of this dependency requirement
     */
    Optional<? extends Scope> getScope();

}
