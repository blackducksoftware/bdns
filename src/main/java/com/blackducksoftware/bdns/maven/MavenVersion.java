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

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Locale.ENGLISH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.blackducksoftware.bdns.Version;

/**
 *
 * @author jgustie
 */
public class MavenVersion implements Version, Comparable<MavenVersion> {

    /**
     * The set of "null values" that can be stripped before each dash delimited token or the end of the token stream.
     */
    private static final Set<String> NULL_VALUES;
    static {
        Set<String> nullValues = new HashSet<>();
        nullValues.add("0");
        nullValues.add("");
        nullValues.add("final");
        nullValues.add("ga");
        NULL_VALUES = unmodifiableSet(nullValues);
    }

    /**
     * The mapping of special qualifiers to their sort order.
     */
    private static final Map<String, Integer> QUALIFIER_ORDER;
    static {
        Map<String, Integer> qualifierOrder = new HashMap<>();
        qualifierOrder.put("alpha", 1);
        qualifierOrder.put("beta", 2);
        qualifierOrder.put("milestone", 3);
        qualifierOrder.put("rc", 4);
        qualifierOrder.put("cr", 4);
        qualifierOrder.put("snapshot", 5);
        qualifierOrder.put("", 6);
        qualifierOrder.put("final", 6);
        qualifierOrder.put("ga", 6);
        qualifierOrder.put("sp", 7);
        QUALIFIER_ORDER = unmodifiableMap(qualifierOrder);
    }

    private final String value;

    private final List<String> tokens;

    private MavenVersion(String value) {
        this.value = Objects.requireNonNull(value);
        this.tokens = tokenize(value);
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals. For example, the versions "1.0.0" and
     * "1.0" are not equal but this method will return 0 because they are the same for the sake of comparison.
     *
     * @see <a href="https://maven.apache.org/pom.html#Version_Order_Specification">Version Order Specification</a>
     */
    @Override
    public int compareTo(MavenVersion o) {
        int result = 0;
        for (int i = 0; i < Math.max(tokens.size(), o.tokens.size()) && result == 0; ++i) {
            String token1 = i < tokens.size() ? tokens.get(i) : null;
            String token2 = i < o.tokens.size() ? o.tokens.get(i) : null;
            if (token1 == null) {
                token1 = token2.startsWith(".") ? ".0" : "-";
            } else if (token2 == null) {
                token2 = token1.startsWith(".") ? ".0" : "-";
            }
            result = compareTokens(token1, token2);
        }
        return result;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MavenVersion) {
            MavenVersion other = (MavenVersion) obj;
            return value.equals(other.value);
        }
        return super.equals(obj);
    }

    public static MavenVersion valueOf(String value) {
        return new MavenVersion(value);
    }

    public static MavenVersion parse(CharSequence value) {
        return new MavenVersion(value.toString());
    }

    /**
     * Given an arbitrary version string, returns the list of sorting tokens.
     */
    private static List<String> tokenize(String value) {
        ArrayList<String> tokens = new ArrayList<>();

        tokens.addAll(Arrays.asList(value.toLowerCase(ENGLISH)
                .replaceAll("(^|[\\.-])a(\\d+)([\\.-]|$)", "$1alpha-$2$3")
                .replaceAll("(^|[\\.-])b(\\d+)([\\.-]|$)", "$1beta-$2$3")
                .replaceAll("(^|[\\.-])m(\\d+)([\\.-]|$)", "$1milestone-$2$3")
                .split("(?=[\\.-])|(?:(?<=\\d)(?=[a-zA-z]))|(?:(?<=[a-zA-Z])(?=\\d))")));

        tokens.subList(1, tokens.size()).replaceAll(s -> s.equals("-") || s.equals(".") ? s + "0" : !s.startsWith("-") && !s.startsWith(".") ? "-" + s : s);

        ListIterator<String> t = tokens.listIterator(tokens.size());
        while (t.hasPrevious() && t.previousIndex() > 0) {
            String token = t.previous();
            if (NULL_VALUES.contains(token.substring(1))) {
                t.remove();
            } else {
                while (!token.startsWith("-") && t.hasPrevious() && t.previousIndex() > 0) {
                    token = t.previous();
                }
            }
        }

        tokens.trimToSize();
        return tokens;
    }

    /**
     * Compares individual tokens.
     */
    private static int compareTokens(String token1, String token2) {
        boolean dot1 = token1.startsWith(".");
        boolean dot2 = token2.startsWith(".");
        boolean dash1 = token1.startsWith("-");
        boolean dash2 = token2.startsWith("-");
        boolean number1 = token1.matches("^[\\.-]?\\d+");
        boolean number2 = token2.matches("^[\\.-]?\\d+");
        if ((dot1 == dot2) && (dash1 == dash2)) {
            token1 = dot1 || dash1 ? token1.substring(1) : token1;
            token2 = dot2 || dash2 ? token2.substring(1) : token2;
            if (number1 && number2) {
                return Long.valueOf(token1).compareTo(Long.valueOf(token2));
            } else if (number1 || number2) {
                return number1 ? 1 : -1;
            } else {
                int qualifier1 = QUALIFIER_ORDER.getOrDefault(token1, 0).intValue();
                int qualifier2 = QUALIFIER_ORDER.getOrDefault(token2, 0).intValue();
                return qualifier1 == 0 && qualifier2 == 0 ? token1.compareTo(token2)
                        : Integer.compare(qualifier1, qualifier2);
            }
        } else {
            return dot1 ? (number1 ? 1 : -1) : (number2 ? -1 : 1);
        }
    }

}
