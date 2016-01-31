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

import io.apiman.cli.support.TestModel;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link MappingUtil}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class MappingUtilTest {
    @Test
    public void testSafeWriteValueAsJson_Populated() throws Exception {
        // test data
        final Map<String, String> input = newHashMap();
        input.put("key", "value");

        // test
        final String actual = MappingUtil.safeWriteValueAsJson(input);

        // assertions
        assertNotNull(actual);
        assertEquals("Object should be serialised to JSON (check ignores whitespace)",
                "{\"key\":\"value\"}", actual.replaceAll("\\s+",""));
    }

    @Test
    public void testSafeWriteValueAsJson_Null() throws Exception {
        // test data
        final Map<String, String> input = null;

        // test
        final String actual = MappingUtil.safeWriteValueAsJson(input);

        // assertions
        assertEquals("null", actual);
    }

    @Test
    public void testMap() throws Exception {
        // test data
        final TestModel input = new TestModel();

        // test
        final TestModel actual = MappingUtil.map(input, TestModel.class);

        // assertions
        assertEquals(input, actual);
    }
}