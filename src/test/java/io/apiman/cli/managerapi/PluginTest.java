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

package io.apiman.cli.managerapi;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.apiman.cli.Cli;
import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.managerapi.core.plugin.model.Plugin;
import io.apiman.cli.util.AuthUtil;
import io.apiman.cli.util.MappingUtil;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.restassured.response.Response;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Category(IntegrationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PluginTest extends BaseTest {
    private static final String PLUGIN_ARTIFACTID = "apiman-plugins-transformation-policy";
    private static final String PLUGIN_GROUPID = "io.apiman.plugins";
    private static final String PLUGIN_VERSION = "1.2.4.Final";

    /**
     * Determine if the given {@link Plugin} matches the expected Maven coordinates.
     *
     * @param plugin the Plugin to check
     * @return {@code true} if plugin matches, otherwise {@code false}
     */
    private boolean isArtifactMatch(Plugin plugin) {
        return PLUGIN_ARTIFACTID.equals(plugin.getArtifactId())
                && PLUGIN_GROUPID.equals(plugin.getGroupId())
                && PLUGIN_VERSION.equals(plugin.getVersion());
    }

    /**
     * @return the plugin expected to have been added
     * @throws java.io.IOException
     */
    @NotNull
    private Plugin getPlugin() throws java.io.IOException {
        // fetch all plugins
        final Response response = given()
                .log().all()
                .header(AuthUtil.HEADER_AUTHORIZATION, AuthUtil.BASIC_AUTH_VALUE)
                .when()
                .get("/plugins")
                .thenReturn();

        assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        @SuppressWarnings("unchecked")
        final List<Plugin> plugins = MappingUtil.JSON_MAPPER.readValue(
                response.body().asByteArray(),
                new TypeReference<List<Plugin>>() {});

        assertNotNull(plugins);
        assertTrue("At least one plugin should be installed", plugins.size() > 0);

        // find the expected plugin
        final Optional<Plugin> addedPlugin = plugins.stream()
                .filter(this::isArtifactMatch)
                .findAny();

        assertTrue(addedPlugin.isPresent());
        return addedPlugin.get();
    }

    /**
     * Adds a plugin to the system. Note: this requires the coordinates of a plugin
     * whose Maven coordinates are accessible in Maven central.
     */
    @Test
    public void test1_create() {
        Cli.main("manager",
                "plugin", "add",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", AuthUtil.DEFAULT_SERVER_USERNAME,
                "--serverPassword", AuthUtil.DEFAULT_SERVER_PASSWORD,
                "--groupId", PLUGIN_GROUPID,
                "--artifactId", PLUGIN_ARTIFACTID,
                "--version", PLUGIN_VERSION);
    }

    @Test
    public void test2_fetch() throws Exception {
        final Plugin addedPlugin = getPlugin();

        // look up plugin by its generated ID
        final Long pluginId = addedPlugin.getId();

        Cli.main("manager",
                "plugin", "show",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", AuthUtil.DEFAULT_SERVER_USERNAME,
                "--serverPassword", AuthUtil.DEFAULT_SERVER_PASSWORD,
                "--id", pluginId.toString());
    }

    @Test
    public void test3_list() {
        Cli.main("manager",
                "plugin", "list",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", AuthUtil.DEFAULT_SERVER_USERNAME,
                "--serverPassword", AuthUtil.DEFAULT_SERVER_PASSWORD);
    }
}
