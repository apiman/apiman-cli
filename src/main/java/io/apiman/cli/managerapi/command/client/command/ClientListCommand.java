/*
 * Copyright 2017 Pete Cornish
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

package io.apiman.cli.managerapi.command.client.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.managerapi.ManagerCommon;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import io.apiman.manager.api.beans.clients.ClientVersionBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

/**
 * List clients
 */
@Parameters(commandDescription = "List Clients")
public class ClientListCommand extends AbstractClientCommand implements GatewayHelper {
    private static final Logger LOGGER = LogManager.getLogger(ClientListCommand.class);

    @Parameter(names = { "--clientName", "-c"}, description = "Client name")
    private String clientName;

    @Parameter(names = { "--version", "-v"} , description = "Client Version")
    private String version;

    private final ManagerCommon config;

    @Inject
    public ClientListCommand(ManagementApiService managementApiService) {
        super(managementApiService);
        config = getManagerConfig();
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Listing {}", this::getModelName);

        ClientApi clientApi = config.buildServerApiClient(ClientApi.class, config.getServerVersion());

        // If Client ID not provided, list all Client in org
        if (clientName == null) {
            print("Clients", () -> clientApi.list(orgName));
        } else if (version == null) { // If version not provided, list all versions of Client
            print("Client Versions", () -> clientApi.listVersions(orgName, clientName));
        } else { // Otherwise retrieve the Client explicitly.
            ClientVersionBean client = callAndCatch(() -> clientApi.fetchVersion(orgName, clientName, version));
            if (client == null) {
                LOGGER.debug("No Client returned for provided parameters");
            } else {
                LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(client));
            }
        }
    }

    private void print(String entityName, Supplier<List<?>> action) {
        LOGGER.debug(entityName);
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(action.get()));
    }
}
