/*
 * Copyright 2016 Andrew Haines
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

import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.util.AuthUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.experimental.categories.Category;

/**
 * @author Andrew Haines {@literal <andrew@haines.org.nz>}
 */
@Category(IntegrationTest.class)
public class ExceptionTest {
    private static final String INVALID_URL = "this is not a valid url";

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testExitWithCode255OnException() {
        exit.expectSystemExitWithStatus(255);

        Cli.main("manager",
                "gateway", "list",
                "--debug",
                "--server", INVALID_URL,
                "--serverUsername", AuthUtil.DEFAULT_SERVER_USERNAME,
                "--serverPassword", AuthUtil.DEFAULT_SERVER_PASSWORD);
    }
}
