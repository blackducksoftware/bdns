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
package com.blackducksoftware.bdns.maven;

import com.blackducksoftware.bdns.Scope;

/**
 *
 * @author jgustie
 * @see <a href="https://maven.apache.org/pom.html#Dependencies">Dependencies</a>
 */
public enum MavenScope implements Scope {

    compile,

    provided,

    runtime,

    test,

    system,

    ;

    public static MavenScope parse(CharSequence value) {
        return valueOf(value.toString());
    }

}
