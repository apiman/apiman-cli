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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GatewayTest extends BaseTest {

    @Test
    public void test1_test() {
        Cli.main("gateway", "test",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--endpoint", "http://localhost:8080/apiman-gateway-api",
                "--username", "apimanager",
                "--password", "apiman123!",
                "--type", "REST");
    }

    @Test
    public void test2_create() {
        Cli.main("gateway", "create",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test",
                "--description", "example",
                "--endpoint", "http://localhost:1234",
                "--username", "apimanager",
                "--password", "apiman123!",
                "--type", "REST");
    }

    @Test
    public void test3_fetch() {
        Cli.main("gateway", "show",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!",
                "--name", "test");
    }

    @Test
    public void test4_list() {
        Cli.main("gateway", "list",
                "--debug",
                "--server", APIMAN_URL,
                "--serverUsername", "admin",
                "--serverPassword", "admin123!");
    }
}
