/*
 * Copyright 2017 Pete Cornish
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

package io.apiman.cli.service;

import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.command.org.OrgApi;
import io.apiman.cli.managerapi.service.ManagementApiServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ManagementApiServiceImpl}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiServiceImplTest {
    private static final String URL = "http://example.com";

    /**
     * Unit under test.
     */
    private ManagementApiServiceImpl service;

    @Before
    public void setUp() throws Exception {
        service = new ManagementApiServiceImpl();
    }

    @Test
    public void testBuildServerApiClient() throws Exception {
        // test
        final OrgApi actual = service.buildServerApiClient(
                OrgApi.class, ManagementApiVersion.UNSPECIFIED, URL, "username", "password", true);

        // assertions
        assertNotNull(actual);
        assertTrue(OrgApi.class.isAssignableFrom(actual.getClass()));
    }
}
