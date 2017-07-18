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
package io.apiman.cli.gatewayapi.command.client;

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
import io.apiman.gateway.engine.beans.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

/**
 * Retrieve information about Clients
 *
 * With arguments:
 * <ul>
 *     <li><tt>--org [foo]</tt>: All clients within org [foo]</li>
 *     <li><tt>--org [foo] --client [bar]</tt>: All versions of client [bar] within org [foo]</li>
 *     <li><tt>--org [foo] --client [bar] --version [baz]</tt>: Retrieve specific client entity</li>
 * </ul>
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@CommandAvailableSince("1.3.2")
@Parameters(commandDescription = "Retrieve information about Clients")
public class ListClientCommand extends AbstractGatewayCommand implements GatewayHelper {

    @Parameter(names = "--org", description = "Organization ID", required = true)
    private String orgId;

    @Parameter(names = "--client", description = "Client App ID")
    private String clientId;

    @Parameter(names = "--version", description = "Client Version")
    private String version;

    private Logger LOGGER = LogManager.getLogger(ListClientCommand.class);

    @Inject
    protected ListClientCommand(GatewayApiService apiFactory) {
        super(apiFactory);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        GatewayApi gatewayApi = getGatewayApiService().buildGatewayApiClient();
        // Do status check
        statusCheck(gatewayApi, getGatewayConfig().getGatewayApiEndpoint());

        // If Client ID not provided, list all Client in org
        if (clientId == null) {
            sortAndPrint("Client", () -> gatewayApi.listClients(orgId));
        } else if (version == null) { // If version not provided, list all versions of Client
            sortAndPrint("Client Versions", () -> gatewayApi.listClientVersions(orgId, clientId));
        } else { // Otherwise retrieve the Client explicitly.
            Client client = callAndCatch(() -> gatewayApi.getClientVersion(orgId, clientId, version));
           if (client == null) {
               LOGGER.debug("No Client returned for provided parameters");
           } else {
               System.out.println( MappingUtil.safeWriteValueAsJson(client));
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
