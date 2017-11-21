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

package io.apiman.cli.service;

import io.apiman.cli.command.common.ActionApi;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.core.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.core.common.util.ServerActionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;

/**
 * Manages APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiServiceImpl implements ApiService {
    private static final Logger LOGGER = LogManager.getLogger(ApiServiceImpl.class);

    private ManagementApiService managementApiService;

    @Inject
    public ApiServiceImpl(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String fetchCurrentState(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        final VersionAgnosticApi apiClient = managementApiService.buildServerApiClient(VersionAgnosticApi.class, serverVersion);

        final String apiState = ofNullable(apiClient.fetchVersion(orgName, apiName, apiVersion).getStatus()).orElse("");
        LOGGER.debug("API '{}' state: {}", apiName, apiState);
        return apiState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        LOGGER.debug("Attempting to publish API: {}", apiName);
        final String apiState = fetchCurrentState(serverVersion, orgName, apiName, apiVersion);

        switch (apiState.toUpperCase()) {
            case STATE_READY:
                performPublish(serverVersion, orgName, apiName, apiVersion);
                break;

            case STATE_PUBLISHED:
                switch (serverVersion) {
                    case v11x:
                        LOGGER.info("API '{}' already published - skipping republish", apiName);
                        break;

                    case v12x:
                        LOGGER.info("Republishing API: {}", apiName);
                        performPublish(serverVersion, orgName, apiName, apiVersion);
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
    private void performPublish(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion) {
        LOGGER.info("Publishing API: {}", apiName);
        ServerActionUtil.publishApi(orgName, apiName, apiVersion, serverVersion,
                managementApiService.buildServerApiClient(ActionApi.class));
    }
}
