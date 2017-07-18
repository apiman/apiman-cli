/*
 * Copyright 2017 Red Hat, Inc.
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
package io.apiman.cli.gatewayapi.command.api;

import io.apiman.cli.Cli;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.gatewayapi.GatewayBaseTest;
import io.apiman.gateway.engine.beans.Api;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.Is.is;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Category(IntegrationTest.class)
public class ApiTest extends GatewayBaseTest {

    /**
     * List APIs within the Org (just 1 in this declaration)
     * @throws Exception ex
     */
    @Test
    public void ListApiBasic_GetApisInOrg() throws Exception {
        applyDeclaration("/gateway/command/api/basic.yml");
        Assert.assertThat(listApisInOrg("woohooapiman"), is("mycoolapi"));
    }

    /**
     * List Api Versions for specified API (just 1 in this declaration)
     * @throws Exception ex
     */
    @Test
    public void ListApiBasic_GetApiVersions() throws Exception {
        applyDeclaration("/gateway/command/api/basic.yml");
        Assert.assertThat(listApiVersions("woohooapiman", "mycoolapi"), is("1.0"));
    }

    /**
     * Get the specific API entity (i.e. as JSON)
     * @throws Exception ex
     */
    @Test
    public void ListApiBasic_GetEntity() throws Exception {
        applyDeclaration("/gateway/command/api/basic.yml");
        // Call Gateway API to see whether entity is there
        Api remoteApi = getApi("woohooapiman", "mycoolapi", "1.0");
        // And whether it has the values we expect
        Api expectApi = expectJson("/gateway/command/api/basic-expectation.json", Api.class);
        Assert.assertThat(expectApi, samePropertyValuesAs(remoteApi));
    }

    private String listApisInOrg(String org) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream currentOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Cli.main("gateway",
                "api",
                "list",
                "--org", org,
                "--server", getApimanUrl(),
                "--serverUsername", APIMAN_USERNAME,
                "--serverPassword", APIMAN_PASSWORD);

        System.setOut(currentOut);
        return new String(outputStream.toByteArray()).trim();
    }

    private String listApiVersions(String org, String apiId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream currentOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Cli.main("gateway",
                "api",
                "list",
                "--org", org,
                "--api", apiId,
                "--server", getApimanUrl(),
                "--serverUsername", APIMAN_USERNAME,
                "--serverPassword", APIMAN_PASSWORD);

        System.setOut(currentOut);
        return new String(outputStream.toByteArray()).trim();
    }

}
