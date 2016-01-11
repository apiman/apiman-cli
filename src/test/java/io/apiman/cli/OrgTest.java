/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrgTest extends BaseTest {

    @Test
    public void test1_create() {
        Cli.main("org", "create",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test",
                "--description", "example");
    }

    @Test
    public void test2_fetch() {
        Cli.main("org", "show",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test");
    }
}
