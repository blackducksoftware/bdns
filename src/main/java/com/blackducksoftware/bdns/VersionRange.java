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

import java.util.function.Predicate;

/**
 * A version range defines a request for one or more versions. Generally build systems will allow a range of versions to
 * be requested and only a single version to be used (potentially scoped). Some namespaces only allow a range consisting
 * of a single version to be specified while others may support complex expressions to define the range of versions.
 *
 * @author jgustie
 */
public interface VersionRange extends Predicate<Version> {

}
