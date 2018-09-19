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

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@code SemVer}.
 *
 * @author jgustie
 */
public class SemVerTest {

    @Test
    public void normalVersionNumber_examples() {
        assertThat(Arrays.asList(
                SemVer.valueOf("1.9.0"),
                SemVer.valueOf("1.10.0"),
                SemVer.valueOf("1.11.0"))).isOrdered();
    }

    @Test
    public void preReleaseVersion_examples() {
        assertThat(SemVer.valueOf("1.0.0-alpha").getPreReleaseVersion()).containsExactly("alpha").inOrder();
        assertThat(SemVer.valueOf("1.0.0-alpha.1").getPreReleaseVersion()).containsExactly("alpha", "1").inOrder();
        assertThat(SemVer.valueOf("1.0.0-0.3.7").getPreReleaseVersion()).containsExactly("0", "3", "7").inOrder();
        assertThat(SemVer.valueOf("1.0.0-x.7.z.92").getPreReleaseVersion()).containsExactly("x", "7", "z", "92").inOrder();
    }

    @Test
    public void buildMetadata_examples() {
        assertThat(SemVer.valueOf("1.0.0-alpha+001").getBuildMetadata()).containsExactly("001").inOrder();
        assertThat(SemVer.valueOf("1.0.0+20130313144700").getBuildMetadata()).containsExactly("20130313144700").inOrder();
        assertThat(SemVer.valueOf("1.0.0-beta+exp.sha.5114f85").getBuildMetadata()).containsExactly("exp", "sha", "5114f85").inOrder();
    }

    @Test
    public void precedence_examples() {
        assertThat(Arrays.asList(
                SemVer.valueOf("1.0.0"),
                SemVer.valueOf("2.0.0"),
                SemVer.valueOf("2.1.0"),
                SemVer.valueOf("2.1.1"))).isOrdered();

        assertThat(Arrays.asList(
                SemVer.valueOf("1.0.0-alpha"),
                SemVer.valueOf("1.0.0"))).isOrdered();

        assertThat(Arrays.asList(
                SemVer.valueOf("1.0.0-alpha"),
                SemVer.valueOf("1.0.0-alpha.1"),
                SemVer.valueOf("1.0.0-alpha.beta"),
                SemVer.valueOf("1.0.0-beta"),
                SemVer.valueOf("1.0.0-beta.2"),
                SemVer.valueOf("1.0.0-beta.11"),
                SemVer.valueOf("1.0.0-rc.1"),
                SemVer.valueOf("1.0.0"))).isOrdered();
    }

}
