/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.command;

import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.core.declarative.command.GatewayApplyCommand;
import io.apiman.cli.util.LogUtil;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GatewayDeclarativeTest extends BaseTest {
    private static final boolean LOG_DEBUG = true;

    private GatewayApplyCommand command;

    @Before
    public void setUp() {
        command = new GatewayApplyCommand();
        // configure logging level
        command.setLogDebug(LOG_DEBUG);
        LogUtil.configureLogging(LOG_DEBUG);
    }

    @Test
    public void testApplyDeclaration_PluginAndBuiltInPolicies() throws Exception {
        command.setDeclarationFile(Paths.get(GatewayDeclarativeTest.class.getResource("/gateway-simple-plugin-and-builtin.yml").toURI()));
        command.applyDeclaration();
    }
}
