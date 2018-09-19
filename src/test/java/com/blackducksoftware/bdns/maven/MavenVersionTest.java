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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@code MavenVersion}.
 *
 * @author jgustie
 */
public class MavenVersionTest {

    @Test
    public void versionOrderSpecification_examples_numberPadding() {
        assertThat(v("1")).isLessThan(v("1.1"));
    }

    @Test
    public void versionOrderSpecification_examples_qualifierPadding() {
        assertThat(v("1-snapshot")).isLessThan(v("1"));
        assertThat(v("1")).isLessThan(v("1-sp"));
    }

    @Test
    public void versionOrderSpecification_examples_correctlyAutomaticallySwitchingToNumericOrder() {
        assertThat(v("1-foo2")).isLessThan(v("1-foo10"));
    }

    @Test
    public void versionOrderSpecification_examples_4() {
        assertThat(v("1.foo")).isLessThan(v("1-foo"));
        assertThat(v("1-foo")).isLessThan(v("1-1"));
        assertThat(v("1-1")).isLessThan(v("1.1"));
    }

    @Test
    public void versionOrderSpecification_examples_removingOfTrailingNullValues() {
        assertThat(v("1.ga")).isEquivalentAccordingToCompareTo(v("1-ga"));
        assertThat(v("1-ga")).isEquivalentAccordingToCompareTo(v("1-0"));
        assertThat(v("1-0")).isEquivalentAccordingToCompareTo(v("1.0"));
        assertThat(v("1.0")).isEquivalentAccordingToCompareTo(v("1"));
    }

    @Test
    public void versionOrderSpecification_examples_6() {
        assertThat(v("1-sp")).isGreaterThan(v("1-ga"));
    }

    @Test
    public void versionOrderSpecification_examples_7() {
        assertThat(v("1-sp.1")).isGreaterThan(v("1-ga.1"));
    }

    @Test
    public void versionOrderSpecification_examples_trailingNullValuesAtEachHyphen() {
        assertThat(v("1-sp-1")).isLessThan(v("1-ga-1"));
        assertThat(v("1-ga-1")).isEquivalentAccordingToCompareTo(v("1-1"));
    }

    @Test
    public void versionOrderSpecification_examples_9() {
        assertThat(v("1-a1")).isEquivalentAccordingToCompareTo(v("1-alpha-1"));
    }

    // Readability helper
    private static MavenVersion v(String value) {
        return MavenVersion.valueOf(value);
    }

}
