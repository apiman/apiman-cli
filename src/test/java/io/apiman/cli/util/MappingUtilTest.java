/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link MappingUtil}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class MappingUtilTest {
    private static final String INPUT_STRING = "This is a ${key1} sample ${key2} string.";

    @Test
    public void testResolvePlaceholders_Success() throws Exception {
        // test data
        final String original = INPUT_STRING;

        final String[] replacements = new String[] {
                "key1=value1",
                "key2=value2"
        };

        // test
        final String actual = MappingUtil.resolvePlaceholders(original, replacements);

        // assertions
        assertEquals("This is a value1 sample value2 string.", actual);
    }

    @Test
    public void testResolvePlaceholders_EmptyReplacements() throws Exception {
        // test data
        final String original = INPUT_STRING;

        final String[] replacements = new String[0];

        // test
        final String actual = MappingUtil.resolvePlaceholders(original, replacements);

        // assertions
        assertEquals(INPUT_STRING, actual);
    }

    @Test
    public void testResolvePlaceholders_NullReplacements() throws Exception {
        // test data
        final String original = INPUT_STRING;

        final String[] replacements = null;

        // test
        final String actual = MappingUtil.resolvePlaceholders(original, replacements);

        // assertions
        assertEquals(INPUT_STRING, actual);
    }
}