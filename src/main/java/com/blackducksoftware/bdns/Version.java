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

/**
 * A version number or tag applied to a component at a specific point in it's lifecycle. Some namespaces define specific
 * comparison, equivalence and or canonicalization rules; other namespaces may treat the version as an arbitrary string.
 *
 * @implNote Bare instances of version are not comparable, if a specific namespace defines an explicit version order
 *           then it is up to the implementation to implement the {@code Comparable} interface.
 *
 * @author jgustie
 */
public interface Version {

}
