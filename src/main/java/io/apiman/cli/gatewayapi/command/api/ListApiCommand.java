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
import com.google.inject.Inject;
import io.apiman.cli.annotations.CommandAvailableSince;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.gatewayapi.GatewayApi;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.gatewayapi.command.common.AbstractGatewayCommand;
import io.apiman.cli.gatewayapi.command.factory.GatewayApiService;
import io.apiman.cli.util.MappingUtil;
import io.apiman.gateway.engine.beans.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

/**
 * Retrieve information about APIs.
 *
 * With arguments:
 * <ul>
 *     <li><tt>--org [foo]</tt>: All APIs within org [foo]</li>
 *     <li><tt>--org [foo] --api [bar]</tt>: All versions of Api [bar] within org [foo]</li>
 *     <li><tt>--org [foo] --api [bar] --version [baz]</tt>: Retrieve specific API entity</li>
 * </ul>
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@CommandAvailableSince("1.3.2")
@Parameters(commandDescription = "Retrieve information about APIs")
public class ListApiCommand extends AbstractGatewayCommand implements GatewayHelper {

    @Parameter(names = "--org", description = "Organization ID", required = true)
    private String orgId;

    @Parameter(names = "--api", description = "API ID")
    private String apiId;

    @Parameter(names = "--version", description = "API Version")
    private String version;

    private Logger LOGGER = LogManager.getLogger(ListApiCommand.class);

    @Inject
    protected ListApiCommand(GatewayApiService apiFactory) {
        super(apiFactory);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        GatewayApi gatewayApi = getGatewayApiService().buildGatewayApiClient();
        // Do status check
        statusCheck(gatewayApi, getGatewayConfig().getGatewayApiEndpoint());

        // If API ID not provided, list all APIs in org
        if (apiId == null) {
            sortAndPrint("APIs", () -> gatewayApi.listApis(orgId));
        } else if (version == null) { // If version not provided, list all versions of API
            sortAndPrint("API Versions", () -> gatewayApi.listApiVersions(orgId, apiId));
        } else { // Otherwise retrieve the API explicitly.
            Api api = callAndCatch(() -> gatewayApi.getApiVersion(orgId, apiId, version));

           if (api == null) {
               LOGGER.debug("No API returned for provided parameters");
           } else {
               System.out.println( MappingUtil.safeWriteValueAsJson(api));
           }
        }
    }

    private void sortAndPrint(String entityName, Supplier<List<String>> action) {
        List<String> ids = callAndCatch(action);
        LOGGER.debug("{} returned: {}", entityName, ids.size());
        // Sort case insensitively
        ids.sort(String::compareToIgnoreCase);
        ids.forEach(System.out::println);
    }
}
