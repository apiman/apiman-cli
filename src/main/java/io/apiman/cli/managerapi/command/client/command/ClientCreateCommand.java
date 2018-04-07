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
import io.apiman.cli.command.client.model.Client;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.ManagerCommon;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.managerapi.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Create an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Create a Client")
public class ClientCreateCommand extends AbstractClientCommand {
    private static final Logger LOGGER = LogManager.getLogger(ClientCreateCommand.class);

    @Parameter(names = {"--name", "-n"}, description = "Client name", required = true)
    private String name;

    @Parameter(names = {"--description", "-d"}, description = "Description")
    private String description;

    @Parameter(names = {"--initialVersion", "-v"}, description = "Initial version", required = true)
    private String initialVersion;

    private final ManagerCommon manager;

    @Inject
    public ClientCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
        manager = getManagerConfig();
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Creating {}", this::getModelName);

        final Client client = new Client(
                name,
                description,
                initialVersion);

        ClientApi clientApi = manager.buildServerApiClient(ClientApi.class, manager.getServerVersion());
        ManagementApiUtil.invokeAndCheckResponse(() -> clientApi.create(orgName, client));
    }
}
