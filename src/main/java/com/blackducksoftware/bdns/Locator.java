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
 * A locator represents the actual location of an individual package. Using a locator with the appropriate protocol
 * specific resolver, it is possible to obtain a copy of the package or artifact itself. Most often a locator will be
 * represented as a HTTP(S) URI, however other protocols may be used.
 *
 * @author jgustie
 */
public interface Locator {

    /**
     * Extracts the identifier portion of this locator.
     *
     * @return the identifier associated with this locator
     */
    Identifier identifier();

    /**
     * Extracts the context portion of this locator.
     *
     * @return the context associated with this locator
     */
    Context context();

    /**
     * Returns this locator as a URI.
     * 
     * @return the URI representing this locator.
     */
    String toUriString();

}
