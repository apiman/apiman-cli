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

package io.apiman.cli.action;

import com.google.common.collect.Lists;
import io.apiman.cli.common.BaseTest;
import io.apiman.cli.core.common.model.ServerVersion;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.core.declarative.action.ApplyAction;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.core.declarative.model.DeclarativeGateway;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link ApplyAction}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Category(IntegrationTest.class)
public class DeclarativeTest extends BaseTest {
    private static final boolean LOG_DEBUG = true;

    private ApplyAction action;

    @Before
    public void setUp() {
        action = new ApplyAction();
        action.setServerAddress(getApimanUrl());

        // version specific test
        action.setServerVersion(ServerVersion.v11x);

        // configure logging level
        action.setLogDebug(LOG_DEBUG);
        LogUtil.configureLogging(LOG_DEBUG);
    }

    /**
     * Expect that the declarative model can be loaded from a JSON file.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationJson() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-full.json").toURI()), MappingUtil.JSON_MAPPER);

        assertLoadedModel(declaration);
    }

    /**
     * Expect that the declarative model can be loaded from a YAML file.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationYaml() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-full.yml").toURI()), MappingUtil.YAML_MAPPER);

        assertLoadedModel(declaration);
    }

    /**
     * Expect that the declarative model can be loaded from a YAML file containing placeholders.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationPlaceholders() throws Exception {
        // set properties
        action.setProperties(Lists.newArrayList(
            "gw.endpoint=http://example.com",
            "gw.username=myuser",
            "gw.password=secret"
        ));

        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-placeholders.yml").toURI()), MappingUtil.YAML_MAPPER);

        // assert loaded with resolved placeholders
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());

        assertNotNull(declaration.getSystem().getGateways());
        assertEquals(1, declaration.getSystem().getGateways().size());

        final DeclarativeGateway gateway = declaration.getSystem().getGateways().get(0);
        assertEquals("http://example.com", gateway.getConfig().getEndpoint());
        assertEquals("myuser", gateway.getConfig().getUsername());
        assertEquals("secret", gateway.getConfig().getPassword());
    }

    /**
     * Expect that the plugins specified in the declaration can be installed.
     *
     * @throws Exception
     */
    @Test
    public void testApplyDeclaration_JustPlugins() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-plugin.yml").toURI()), MappingUtil.YAML_MAPPER);

        action.applyDeclaration(declaration);
    }

    /**
     * Expect that the configuration in the declaration can be applied.
     *
     * @throws Exception
     */
    @Test
    public void testApplyDeclaration_Full() throws Exception {
        final Declaration declaration = action.loadDeclaration(
                Paths.get(DeclarativeTest.class.getResource("/simple-no-plugin.yml").toURI()), MappingUtil.YAML_MAPPER);

        action.applyDeclaration(declaration);
    }

    /**
     * Asserts the contents of the model.
     *
     * @param declaration the model to assert
     */
    private void assertLoadedModel(Declaration declaration) {
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());

        assertNotNull(declaration.getSystem().getGateways());
        assertEquals(1, declaration.getSystem().getGateways().size());

        assertNotNull(declaration.getSystem().getPlugins());
        assertEquals(1, declaration.getSystem().getPlugins().size());

        assertNotNull(declaration.getOrg());
        assertNotNull(declaration.getOrg().getApis());
        assertEquals(1, declaration.getOrg().getApis().size());
    }
}
