/*
 * Copyright 2017 Pete Cornish
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

package io.apiman.cli.managerapi.service;

import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.command.common.ActionApi;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.command.common.util.ServerActionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;

/**
 * Manages APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ClientServiceImpl implements ClientService {
    private static final Logger LOGGER = LogManager.getLogger(ClientServiceImpl.class);

    private ManagementApiService managementApiService;

    @Inject
    public ClientServiceImpl(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fetchCurrentState(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        final ClientApi apiClient = managementApiService.buildServerApiClient(ClientApi.class, serverVersion);
        return ofNullable(apiClient.fetchVersion(orgName, apiName, apiVersion).getStatus().name()).orElse("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        LOGGER.debug("Attempting to publish API: {}", apiName);
        final String apiState = fetchCurrentState(serverVersion, orgName, apiName, apiVersion);

        switch (apiState.toUpperCase()) {
            case STATE_READY:
                performRegister(serverVersion, orgName, apiName, apiVersion);
                break;

            case STATE_REGISTERED:
                switch (serverVersion) {
                    case v11x:
                        LOGGER.info("Client '{}' already published - skipping republish", apiName);
                        break;

                    case v12x:
                        LOGGER.info("Republishing API: {}", apiName);
                        performRegister(serverVersion, orgName, apiName, apiVersion);
                        break;
                }
                break;

            default:
                throw new CommandException(String.format(
                        "Unable to publish API '%s' in state: %s", apiName, apiState));
        }
    }

    /**
     * Trigger the publish action for the given API.
     *
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void performRegister(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        LOGGER.info("Registering Client: {}", apiName);
        ServerActionUtil.registerClient(orgName, apiName, apiVersion, serverVersion,
                managementApiService.buildServerApiClient(ActionApi.class));
    }
}
