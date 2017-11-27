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
package io.apiman.cli.gatewayapi.command.apply;

import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.gatewayapi.GatewayBaseTest;
import io.apiman.gateway.engine.beans.Api;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.samePropertyValuesAs;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Category(IntegrationTest.class)
public class ApplyTest extends GatewayBaseTest {

    @Test
    public void ApplyDefinition_SingleApi() throws Exception {
        applyDeclaration("/gateway/command/apply/basic.yml");
        // Call Gateway API to see whether entity is there
        Api remoteApi = getApi("theorgname", "mycoolapi", "1.0");
        // And whether it has the values we expect
        Api expectApi = expectJson("/gateway/command/apply/basic-expectation.json", Api.class);
        Assert.assertThat(expectApi, samePropertyValuesAs(remoteApi));
    }

    @Test
    public void ApplyDefinition_MultipleApis() throws Exception {
        applyDeclaration("/gateway/command/apply/multi-version.yml");

        // Call Gateway API to see whether entities are there
        Api remoteApi1 = getApi("seychelles", "mahe", "1.0");
        Api remoteApi2 = getApi("seychelles", "mahe", "2.0");

        // And whether they have the expected values
        Api expectApi1 = expectJson("/gateway/command/apply/multi-version-expectation-1.json", Api.class);
        Api expectApi2 = expectJson("/gateway/command/apply/multi-version-expectation-2.json", Api.class);

        Assert.assertThat(expectApi1, samePropertyValuesAs(remoteApi1));
        Assert.assertThat(expectApi2, samePropertyValuesAs(remoteApi2));
    }

}
