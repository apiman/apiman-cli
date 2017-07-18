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

import com.google.common.collect.ImmutableMap;
import io.apiman.cli.command.ManagerDeclarativeTest;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.core.declarative.model.DeclarativeGateway;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link DeclarativeUtil}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeUtilTest {

    /**
     * Expect that the declarative model can be loaded from a JSON file.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationJson() throws Exception {
        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/simple-full.json").toURI()), MappingUtil.JSON_MAPPER,
                Collections.emptyMap());

        assertLoadedModel(declaration, 1);
    }

    /**
     * Expect that the declarative model can be loaded from a YAML file.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationYaml() throws Exception {
        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/simple-full.yml").toURI()), MappingUtil.YAML_MAPPER,
                Collections.emptyMap());

        assertLoadedModel(declaration, 1);
    }

    /**
     * Expect that the declarative model can be loaded from a YAML file containing placeholders.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationPlaceholders() throws Exception {
        // set properties
        final Map<String, String> properties = ImmutableMap.of(
                "gw.endpoint", "http://example.com",
                "gw.username", "myuser",
                "gw.password", "secret"
        );

        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/simple-placeholders.yml").toURI()), MappingUtil.YAML_MAPPER, properties);

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
     * Expect that the declarative model can be loaded from a YAML file containing placeholders and embedded properties.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationSharedProperties() throws Exception {
        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/shared-properties.yml").toURI()),
                MappingUtil.YAML_MAPPER, Collections.emptyMap());

        // assert loaded with resolved placeholders
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());

        assertNotNull(declaration.getSystem().getGateways());
        assertEquals(1, declaration.getSystem().getGateways().size());

        final DeclarativeGateway gateway = declaration.getSystem().getGateways().get(0);
        assertEquals("value1", gateway.getConfig().getEndpoint());
        assertEquals("value2", gateway.getConfig().getUsername());
        assertEquals("value3", gateway.getConfig().getPassword());
    }

    /**
     * Expect that the declarative model can be loaded from a JSON file with shared items.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationSharedJson() throws Exception {
        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/shared-policies.json").toURI()), MappingUtil.JSON_MAPPER,
                Collections.emptyMap());

        assertLoadedModel(declaration, 2);
    }

    /**
     * Expect that the declarative model can be loaded from a YAML file with shared items.
     *
     * @throws Exception
     */
    @Test
    public void testLoadDeclarationSharedYaml() throws Exception {
        final BaseDeclaration declaration = DeclarativeUtil.loadDeclaration(
                Paths.get(ManagerDeclarativeTest.class.getResource("/shared-policies.yml").toURI()), MappingUtil.YAML_MAPPER,
                Collections.emptyMap());

        assertLoadedModel(declaration, 2);
    }

    /**
     * Asserts the contents of the model.
     *
     * @param declaration      the model to assert
     * @param expectedApiCount the number of API items
     */
    private void assertLoadedModel(BaseDeclaration declaration, int expectedApiCount) {
        assertNotNull(declaration);
        assertNotNull(declaration.getSystem());

        assertNotNull(declaration.getSystem().getGateways());
        assertEquals(1, declaration.getSystem().getGateways().size());

        assertNotNull(declaration.getSystem().getPlugins());
        assertEquals(1, declaration.getSystem().getPlugins().size());

        assertNotNull(declaration.getOrg());
        assertNotNull(declaration.getOrg().getApis());
        assertEquals(expectedApiCount, declaration.getOrg().getApis().size());

        assertNotNull(declaration.getOrg().getApis().get(0).getConfig());
        assertNotNull(declaration.getOrg().getApis().get(0).getConfig().getSecurity());
    }
}