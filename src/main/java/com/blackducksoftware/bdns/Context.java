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
 * Generally an identifier is a relative description of a component, a context is the base that is used to produce an
 * absolute identifier. Typically a context will represent either a base URL or a URL template that can be used in
 * conjunction with a (possibly decomposed) identifier to produce a resolvable URL (generally using HTTP); though some
 * systems may simply have simple tokens as a context, requiring lookup tables or other means to produce an absolute
 * identifier.
 *
 * @author jgustie
 */
public interface Context {

}
