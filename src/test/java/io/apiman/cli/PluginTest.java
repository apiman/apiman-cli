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

import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.IntegrationTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Category(IntegrationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PluginTest extends BaseTest {

    /**
     * Adds a plugin to the system. Note: this requires the coordinates of a plugin
     * whose Maven coordinates are accessible in Maven central.
     */
    @Test
    public void test1_create() {
        Cli.main("plugin", "add",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--groupId", "io.apiman.plugins",
                "--artifactId", "apiman-plugins-transformation-policy",
                "--version", "1.2.1.Final");
    }

    @Test
    public void test2_fetch() {
        Cli.main("plugin", "show",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--id", "1");
    }

    @Test
    public void test3_list() {
        Cli.main("plugin", "list",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", "admin",
                "--serverPassword", "admin123!");
    }
}
