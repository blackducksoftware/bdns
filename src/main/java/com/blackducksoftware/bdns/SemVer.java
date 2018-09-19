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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A version for software that uses Semantic Versioning.
 *
 * @author jgustie
 * @see <a href="https://semver.org/spec/v2.0.0.html">Semantic Versioning 2.0.0</a>
 */
public class SemVer implements Version, Comparable<SemVer> {

    /**
     * Constant for version {@code 1.0.0}.
     */
    private static final SemVer PUBLIC_API = new Builder().version(1, 0, 0).build();

    /**
     * Version defining the public API (1.0.0).
     *
     * @see <a href="https://semver.org/spec/v2.0.0.html#spec-item-5">Section 5</a>
     */
    public static SemVer publicApi() {
        return PUBLIC_API;
    }

    /**
     * Cached {@code toString} representation, may be {@code null}. This will be pre-populated by {@code valueOf}.
     */
    private String value;

    private final int majorVersion;

    private final int minorVersion;

    private final int patchVersion;

    private final List<String> preReleaseVersion;

    private final List<String> buildMetadata;

    protected SemVer(Builder builder) {
        this.value = builder.value;
        this.majorVersion = builder.majorVersion;
        this.minorVersion = builder.minorVersion;
        this.patchVersion = builder.patchVersion;
        this.preReleaseVersion = Rules.unmodifiableCopyOf(builder.preReleaseVersion);
        this.buildMetadata = Rules.unmodifiableCopyOf(builder.buildMetadata);
    }

    public final int getMajorVersion() {
        return majorVersion;
    }

    public final int getMinorVersion() {
        return minorVersion;
    }

    public final int getPatchVersion() {
        return patchVersion;
    }

    public final List<String> getPreReleaseVersion() {
        return preReleaseVersion;
    }

    public final List<String> getBuildMetadata() {
        return buildMetadata;
    }

    public final boolean isPublicApi() {
        return getMajorVersion() >= 1;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion, preReleaseVersion, buildMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SemVer) {
            SemVer o = (SemVer) obj;
            return majorVersion == o.majorVersion
                    && minorVersion == o.minorVersion
                    && patchVersion == o.patchVersion
                    && preReleaseVersion.equals(o.preReleaseVersion)
                    // This is inconsistent with `compareTo`
                    && buildMetadata.equals(o.buildMetadata);
        }
        return false;
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals. Build metadata is not considered as
     * part of the natural ordering but is used for equality checks.
     *
     * @see <a href="https://semver.org/spec/v2.0.0.html#spec-item-11">Section 11</a>
     */
    @Override
    public int compareTo(SemVer o) {
        int result;
        if ((result = Integer.compare(majorVersion, o.majorVersion)) != 0) {
            return result;
        }
        if ((result = Integer.compare(minorVersion, o.minorVersion)) != 0) {
            return result;
        }
        if ((result = Integer.compare(patchVersion, o.patchVersion)) != 0) {
            return result;
        }

        int preReleaseVersionLen = Math.min(preReleaseVersion.size(), o.preReleaseVersion.size());
        if (preReleaseVersionLen > 0) {
            // Neither pre-release version is empty
            for (int i = 0; i < preReleaseVersionLen; ++i) {
                if ((result = Rules.compareIdentifier(preReleaseVersion.get(i), o.preReleaseVersion.get(i))) != 0) {
                    return result;
                }
            }
            return Integer.compare(preReleaseVersion.size(), o.preReleaseVersion.size());
        } else if (preReleaseVersion.isEmpty()) {
            return o.preReleaseVersion.isEmpty() ? 0 : 1;
        } else {
            return o.preReleaseVersion.isEmpty() ? -1 : 0;
        }
    }

    @Override
    public String toString() {
        if (value != null) {
            return value;
        }

        StringJoiner preReleaseVersionString = new StringJoiner(".", "-", "").setEmptyValue("");
        preReleaseVersion.forEach(preReleaseVersionString::add);

        StringJoiner buildMetadataString = new StringJoiner(".", "+", "").setEmptyValue("");
        buildMetadata.forEach(buildMetadataString::add);

        return value = new StringBuilder(16 + preReleaseVersionString.length() + buildMetadataString.length())
                .append(majorVersion)
                .append('.')
                .append(minorVersion)
                .append('.')
                .append(patchVersion)
                .append(preReleaseVersionString)
                .append(buildMetadataString)
                .toString();
    }

    public static SemVer valueOf(CharSequence input) {
        Matcher matcher = Rules.tokenize(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("invalid Semver input: " + input);
        }

        Builder builder = new Builder(input.toString());
        builder.majorVersion(Integer.parseInt(matcher.group(1)));
        builder.minorVersion(Integer.parseInt(matcher.group(2)));
        builder.patchVersion(Integer.parseInt(matcher.group(3)));
        builder.preReleaseVersionString(matcher.group(4));
        builder.buildMetadataString(matcher.group(5));
        return builder.build();
    }

    public static final class Builder {

        private final Rules rules = Rules.VALIDATION;

        private final String value;

        private int majorVersion;

        private int minorVersion;

        private int patchVersion;

        private final List<String> preReleaseVersion;

        private final List<String> buildMetadata;

        public Builder() {
            this((String) null);
        }

        private Builder(SemVer semVer) {
            value = null;
            this.majorVersion = semVer.majorVersion;
            this.minorVersion = semVer.minorVersion;
            this.patchVersion = semVer.patchVersion;
            this.preReleaseVersion = new ArrayList<>(semVer.preReleaseVersion);
            this.buildMetadata = new ArrayList<>(semVer.buildMetadata);
        }

        private Builder(String value) {
            this.value = value;
            preReleaseVersion = new ArrayList<>();
            buildMetadata = new ArrayList<>();
        }

        public Builder majorVersion(int majorVersion) {
            this.majorVersion = rules.checkVersion(majorVersion, "major version");
            return this;
        }

        public Builder minorVersion(int minorVersion) {
            this.minorVersion = rules.checkVersion(minorVersion, "minor version");
            return this;
        }

        public Builder patchVersion(int patchVersion) {
            this.patchVersion = rules.checkVersion(patchVersion, "patch version");
            return this;
        }

        public Builder preReleaseVersion(List<String> preReleaseVersion) {
            this.preReleaseVersion.clear();
            preReleaseVersion.forEach(i -> rules.addPreReleaseVersion(this.preReleaseVersion, i));
            return this;
        }

        public Builder buildMetadata(List<String> buildMetadata) {
            this.buildMetadata.clear();
            buildMetadata.forEach(i -> rules.addBuildMetadata(this.buildMetadata, i));
            return this;
        }

        public Builder version(int major, int minor, int patch) {
            return majorVersion(major).minorVersion(minor).patchVersion(patch);
        }

        public Builder preReleaseVersion(String identifier, String... identifiers) {
            preReleaseVersion.clear();
            rules.addPreReleaseVersion(preReleaseVersion, identifier);
            for (String i : identifiers) {
                rules.addPreReleaseVersion(preReleaseVersion, i);
            }
            return this;
        }

        public Builder buildMetadata(String identifier, String... identifiers) {
            buildMetadata.clear();
            rules.addBuildMetadata(buildMetadata, identifier);
            for (String i : identifiers) {
                rules.addBuildMetadata(buildMetadata, i);
            }
            return this;
        }

        private Builder preReleaseVersionString(String value) {
            return preReleaseVersion(value != null ? Arrays.asList(value.split("\\.", -1)) : Collections.emptyList());
        }

        private Builder buildMetadataString(String value) {
            return buildMetadata(value != null ? Arrays.asList(value.split("\\.", -1)) : Collections.emptyList());
        }

        public Builder incrementPatchVersion() {
            return version(majorVersion, minorVersion, patchVersion + 1);
        }

        public Builder incrementMinorVersion() {
            return version(majorVersion, minorVersion + 1, 0);
        }

        public Builder incrementMajorVersion() {
            return version(majorVersion + 1, 0, 0);
        }

        public SemVer build() {
            return new SemVer(this);
        }
    }

    /**
     * Definitions for the various rules needed to work with semantic versions.
     */
    private static final class Rules {

        /**
         * Default instance of rules that only performs validation.
         */
        public static final Rules VALIDATION = new Rules();

        /**
         * This pattern is not strict enough for validation, it is only suitable for tokenization.
         */
        private static final Pattern TOKEN_PATTERN = Pattern.compile("([0-9]+).([0-9]+).([0-9]+)(?:\\-([0-9A-Za-z-.]*))?(?:\\+([0-9A-Za-z-.]*))?");

        public int checkVersion(int version, String message) {
            if (version < 0) {
                throw new IllegalArgumentException(message + " must be non-negative: " + version);
            }
            return version;
        }

        public void addPreReleaseVersion(List<String> preReleaseVersion, String identifier) {
            preReleaseVersion.add(checkIdentifier(identifier, false, "pre-release version"));
        }

        public void addBuildMetadata(List<String> buildMetadata, String identifier) {
            buildMetadata.add(checkIdentifier(identifier, true, "build metadata"));
        }

        private String checkIdentifier(String identifier, boolean allowLeadingZero, String message) {
            int len = identifier.length();
            if (len == 0) {
                throw new IllegalArgumentException(message + " identifier must not be empty");
            }

            boolean numeric = true;
            for (int i = 0; i < len; ++i) {
                char c = identifier.charAt(i);
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '-') {
                    numeric = false;
                } else if (c < '0' || c > '9') {
                    throw new IllegalArgumentException(message + " identifier must be alphanumeric or hyphen: " + identifier);
                }
            }
            if (numeric && !allowLeadingZero && len > 1 && identifier.charAt(0) == '0') {
                throw new IllegalArgumentException(message + " identifier must not include leading zeros: " + identifier);
            }

            return identifier;
        }

        public static Matcher tokenize(CharSequence input) {
            return TOKEN_PATTERN.matcher(input);
        }

        public static int compareIdentifier(String i1, String i2) {
            int ni1;
            int ni2;
            try {
                ni1 = Integer.parseInt(i1);
            } catch (NumberFormatException e) {
                ni1 = -1;
            }
            try {
                ni2 = Integer.parseInt(i2);
            } catch (NumberFormatException e) {
                ni2 = -1;
            }
            if (ni1 < 0 && ni2 < 0) {
                return i1.compareTo(i2);
            } else if (ni1 < 0) {
                return 1;
            } else if (ni2 < 0) {
                return -1;
            } else {
                return Integer.compare(ni1, ni2);
            }
        }

        public static List<String> unmodifiableCopyOf(List<String> source) {
            if (source.isEmpty()) {
                return Collections.emptyList();
            } else {
                return Collections.unmodifiableList(Arrays.asList(source.toArray(new String[source.size()])));
            }
        }
    }

}
