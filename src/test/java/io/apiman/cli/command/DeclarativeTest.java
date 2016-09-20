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

package io.apiman.cli.command;

import com.google.common.collect.Lists;
import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.core.common.model.ManagementApiVersion;
import io.apiman.cli.core.declarative.command.ApplyCommand;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.util.DeclarativeUtil;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Tests for {@link ApplyCommand}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Category(IntegrationTest.class)
public class DeclarativeTest extends BaseTest {
    private static final boolean LOG_DEBUG = true;

    private ApplyCommand command;

    @Before
    public void setUp() {
        command = new ApplyCommand();
        command.setServerAddress(getApimanUrl());

        // version specific test
        command.setServerVersion(ManagementApiVersion.v12x);

        // configure logging level
        command.setLogDebug(LOG_DEBUG);
        LogUtil.configureLogging(LOG_DEBUG);
    }

    /**
     * Expect that the plugins specified in the declaration can be installed.
     *
     * @throws Exception
     */
    @Test
    public void testApplyDeclaration_JustPlugins() throws Exception {
        final Declaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-plugin.yml").toURI()), MappingUtil.YAML_MAPPER,
                Collections.emptyMap());

        command.applyDeclaration(declaration);
    }

    /**
     * Expect that the configuration in the declaration can be applied.
     *
     * @throws Exception
     */
    @Test
    public void testApplyDeclaration_Full() throws Exception {
        final Declaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-no-plugin.yml").toURI()), MappingUtil.YAML_MAPPER,
                Collections.emptyMap());

        command.applyDeclaration(declaration);
    }

    /**
     * Expect that the configuration in the declaration can be applied, resolving placeholders passed
     * from:
     * <ul>
     * <li>the command line (using '-P key=value')</li>
     * <li>a key=value format Java properties file (.properties)</li>
     * <li>an XML format Java properties file (.xml)</li>
     * </ul>
     *
     * @throws Exception
     */
    @Test
    public void testApplyDeclaration_WithProperties() throws Exception {
        final List<String> inlineProperties = Lists.newArrayList(
                "gw.endpoint=http://example.com"
        );

        command.setDeclarationFile(Paths.get(DeclarativeTest.class.getResource("/simple-placeholders.yml").toURI()));
        command.setProperties(inlineProperties);
        command.setPropertiesFiles(Lists.newArrayList(
                Paths.get(DeclarativeTest.class.getResource("/placeholder-test.properties").toURI()),
                Paths.get(DeclarativeTest.class.getResource("/placeholder-test.xml").toURI())
        ));

        command.applyDeclaration();
    }
}
