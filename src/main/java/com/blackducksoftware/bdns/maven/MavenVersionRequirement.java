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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.bdns.Version;
import com.blackducksoftware.bdns.VersionRange;

/**
 *
 * @author jgustie
 * @see <a href="https://maven.apache.org/pom.html#Dependency_Version_Requirement_Specification">Dependency Version
 *      Requirement Specification</a>
 */
public class MavenVersionRequirement implements VersionRange {
    // TODO Should this be MavenVersionConstraint?

    private static Pattern RANGE_SET_PATTERN = Pattern.compile("(?<=\\)|\\]),(?=\\(|\\[)");

    private static Pattern RANGE_PATTERN = Pattern.compile("(\\(|\\[)(.*),(.*)(\\)|\\])");

    private static class Empty implements Predicate<MavenVersion> {
        private static final Empty INSTANCE = new Empty();

        private Empty() {
        }

        @Override
        public boolean test(MavenVersion t) {
            return false;
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public int hashCode() {
            return Objects.hash("");
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Empty;
        }
    }

    private static class SoftRequirement implements Predicate<MavenVersion> {
        private final MavenVersion version;

        private SoftRequirement(MavenVersion version) {
            this.version = Objects.requireNonNull(version);
        }

        @Override
        public boolean test(MavenVersion other) {
            return version.compareTo(other) == 0;
        }

        @Override
        public String toString() {
            return version.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(version);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SoftRequirement) {
                return version.equals(((SoftRequirement) obj).version);
            }
            return false;
        }
    }

    private static class HardRequirement implements Predicate<MavenVersion> {
        private final MavenVersion version;

        private HardRequirement(MavenVersion version) {
            this.version = Objects.requireNonNull(version);
        }

        @Override
        public boolean test(MavenVersion other) {
            return version.equals(other);
        }

        @Override
        public String toString() {
            return '[' + version.toString() + ']';
        }

        @Override
        public int hashCode() {
            return Objects.hash(version);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HardRequirement) {
                return version.equals(((HardRequirement) obj).version);
            }
            return false;
        }
    }

    private static class Range implements Predicate<MavenVersion> {
        private final Optional<MavenVersion> lower;

        private final boolean openLower;

        private final Optional<MavenVersion> upper;

        private final boolean openUpper;

        private Range(MavenVersion lower, boolean openLower, MavenVersion upper, boolean openUpper) {
            if (lower == null && upper == null) {
                throw new IllegalArgumentException("both versions in range cannot be null");
            }
            // TODO Also disallow null/false combo?
            this.lower = Optional.ofNullable(lower);
            this.openLower = openLower;
            this.upper = Optional.ofNullable(upper);
            this.openUpper = openUpper;
        }

        @Override
        public boolean test(MavenVersion other) {
            if (lower.isPresent()) {
                int l = lower.get().compareTo(other);
                if (l > 0 || (l == 0 && openLower)) {
                    return false;
                }
            }
            if (upper.isPresent()) {
                int u = upper.get().compareTo(other);
                if (u < 0 || (u == 0 && openUpper)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(openLower ? '(' : '[');
            lower.ifPresent(result::append);
            result.append(',');
            upper.ifPresent(result::append);
            result.append(openUpper ? ')' : ']');
            return result.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(lower, openLower, upper, openUpper);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Range) {
                Range other = (Range) obj;
                return openLower == other.openLower
                        && openUpper == other.openUpper
                        && lower.equals(other.lower)
                        && upper.equals(other.upper);
            }
            return false;
        }
    }

    private static class Multiple implements Predicate<MavenVersion> {
        private final List<Predicate<MavenVersion>> set;

        private Multiple(List<Predicate<MavenVersion>> set) {
            this.set = unmodifiableList(new ArrayList<>(set));
        }

        @Override
        public boolean test(MavenVersion t) {
            return set.stream().map(p -> p.test(t)).filter(Boolean::booleanValue).findFirst().isPresent();
        }

        @Override
        public String toString() {
            return set.stream().map(Object::toString).collect(joining(","));
        }

        @Override
        public int hashCode() {
            return Objects.hash(set.toArray());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Multiple) {
                return set.equals(((Multiple) obj).set);
            }
            return false;
        }
    }

    private final Predicate<MavenVersion> predicate;

    private MavenVersionRequirement(Builder builder) {
        this.predicate = Objects.requireNonNull(builder.predicate);
    }

    @Override
    public boolean test(Version version) {
        return version instanceof MavenVersion ? predicate.test((MavenVersion) version) : false;
    }

    @Override
    public String toString() {
        return predicate.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate.getClass(), predicate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenVersionRequirement) {
            return predicate.equals(((MavenVersionRequirement) obj).predicate);
        }
        return false;
    }

    public static MavenVersionRequirement valueOf(String value) {
        return parse(value);
    }

    public static MavenVersionRequirement parse(CharSequence value) {
        Builder builder = new Builder();
        String[] ranges = RANGE_SET_PATTERN.split(value);
        if (ranges.length == 1) {
            String range = ranges[0];
            if (range.isEmpty()) {
                builder.reset();
            } else if (!range.startsWith("(") && !range.startsWith("[") && !range.endsWith(")") && !range.endsWith("]")) {
                builder.softRequirement(MavenVersion.valueOf(range));
            } else if (range.indexOf(',') < 0) {
                builder.hardRequirement(MavenVersion.parse(range.subSequence(1, range.length() - 1)));
            } else {
                applyRange(builder, range);
            }
        } else {
            for (String range : ranges) {
                applyRange(builder, range);
            }
        }
        return builder.build();
    }

    private static void applyRange(Builder builder, CharSequence input) {
        Matcher m = RANGE_PATTERN.matcher(input);
        if (m.matches()) {
            String lower = m.group(2);
            String upper = m.group(3);
            builder.range(lower.isEmpty() ? null : MavenVersion.valueOf(lower), m.group(1).equals("("),
                    upper.isEmpty() ? null : MavenVersion.valueOf(upper), m.group(4).equals(")"));
        } else {
            throw new IllegalArgumentException("invalid range: " + input);
        }
    }

    public static final class Builder {

        private Predicate<MavenVersion> predicate;

        public Builder() {
            predicate = Empty.INSTANCE;
        }

        public Builder reset() {
            predicate = Empty.INSTANCE;
            return this;
        }

        public Builder softRequirement(MavenVersion version) {
            predicate = new SoftRequirement(version);
            return this;
        }

        public Builder hardRequirement(MavenVersion version) {
            predicate = new HardRequirement(version);
            return this;
        }

        public Builder range(MavenVersion lower, boolean openLower, MavenVersion upper, boolean openUpper) {
            Range range = new Range(lower, openLower, upper, openUpper);
            if (predicate instanceof Empty) {
                predicate = range;
            } else if (predicate instanceof Range || predicate instanceof Multiple) {
                List<Predicate<MavenVersion>> set = new ArrayList<>(predicate instanceof Multiple ? ((Multiple) predicate).set : Collections.emptySet());
                set.add(range);
                predicate = new Multiple(set);
            } else {
                throw new IllegalStateException("cannot append range");
            }
            return this;
        }

        public Builder open(MavenVersion lower, MavenVersion upper) {
            return range(lower, true, upper, true);
        }

        public Builder closed(MavenVersion lower, MavenVersion upper) {
            return range(lower, false, upper, false);
        }

        public Builder closedOpen(MavenVersion lower, MavenVersion upper) {
            return range(lower, false, upper, true);
        }

        public Builder openClosed(MavenVersion lower, MavenVersion upper) {
            return range(lower, true, upper, false);
        }

        // Alternate language methods

        public Builder equalTo(MavenVersion version) {
            return hardRequirement(version);
        }

        public Builder notEqualTo(MavenVersion version) {
            return open(null, version).open(version, null);
        }

        public Builder lessThan(MavenVersion version) {
            return open(null, version);
        }

        public Builder lessThanOrEqualTo(MavenVersion version) {
            return openClosed(null, version);
        }

        public Builder greaterThan(MavenVersion version) {
            return open(version, null);
        }

        public Builder greaterThanOrEqualTo(MavenVersion version) {
            return closedOpen(version, null);
        }

        public MavenVersionRequirement build() {
            return new MavenVersionRequirement(this);
        }

    }

}
