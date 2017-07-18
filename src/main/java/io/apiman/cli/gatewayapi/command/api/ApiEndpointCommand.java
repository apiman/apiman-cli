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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.core.api.GatewayApi;
import io.apiman.cli.core.common.command.AbstractGatewayCommand;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.gateway.engine.beans.ApiEndpoint;

/**
 * Get an API's public endpoint.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Get an API's endpoint")
public class ApiEndpointCommand extends AbstractGatewayCommand implements GatewayHelper {

    @Parameter(names = "--org", description = "Organization ID", required = true)
    private String orgId;

    @Parameter(names = "--api", description = "API ID", required = true)
    private String apiId;

    @Parameter(names = "--version", description = "API Version", required = true)
    private String version;

    @Override
    public void performAction(JCommander parser) throws CommandException {
        GatewayApi gatewayApi = buildGatewayApiClient(getApiFactory(), getGatewayConfig());
        // Do status check
        statusCheck(gatewayApi, getGatewayConfig().getGatewayApiEndpoint());
        // Get endpoint (if any)
        ApiEndpoint endpoint = callAndCatch(getGatewayConfig().getGatewayApiEndpoint(),
                () -> gatewayApi.getApiEndpoint(orgId, apiId, version));
        // Print to syso
        System.out.println(endpoint.getEndpoint());
    }

}
