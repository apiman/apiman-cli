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

import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.ApiVersion;
import io.apiman.cli.command.api.model.EndpointProperties;
import io.apiman.cli.command.declarative.model.DeclarativeApi;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.command.declarative.model.DeclarativeOrg;
import io.apiman.cli.managerapi.core.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.core.gateway.GatewayApi;
import io.apiman.cli.managerapi.core.gateway.model.Gateway;
import io.apiman.cli.managerapi.core.org.OrgApi;
import io.apiman.cli.managerapi.core.org.model.Org;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static io.apiman.cli.managerapi.core.common.model.ManagementApiVersion.v11x;
import static io.apiman.cli.managerapi.core.common.model.ManagementApiVersion.v12x;
import static io.apiman.cli.util.Functions.of;
import static java.util.Optional.ofNullable;

/**
 * Applies changes in a declarative fashion.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeServiceImpl implements DeclarativeService {
    private static final Logger LOGGER = LogManager.getLogger(DeclarativeServiceImpl.class);

    private ManagementApiService managementApiService;
    private ApiService apiService;
    private PolicyService policyService;

    @Inject
    public DeclarativeServiceImpl(ManagementApiService managementApiService,
                                  ApiService apiService, PolicyService policyService) {

        this.managementApiService = managementApiService;
        this.apiService = apiService;
        this.policyService = policyService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyGateways(List<DeclarativeGateway> gateways) {
        LOGGER.debug("Applying gateways");

        gateways.forEach(declarativeGateway -> {
            final GatewayApi apiClient = managementApiService.buildServerApiClient(GatewayApi.class);
            final String gatewayName = declarativeGateway.getName();

            of(ManagementApiUtil.checkExists(() -> apiClient.fetch(gatewayName)))
                    .ifPresent(existing -> {
                        LOGGER.info("Gateway already exists: {}", gatewayName);
                    })
                    .ifNotPresent(() -> {
                        LOGGER.info("Adding gateway: {}", gatewayName);

                        final Gateway gateway = MappingUtil.map(declarativeGateway, Gateway.class);
                        apiClient.create(gateway);
                    });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyOrg(DeclarativeOrg org) {
        LOGGER.debug("Applying org");

        final String orgName = org.getName();
        final OrgApi orgApiClient = managementApiService.buildServerApiClient(OrgApi.class);

        of(ManagementApiUtil.checkExists(() -> orgApiClient.fetch(orgName)))
                .ifPresent(existing -> {
                    LOGGER.info("Org already exists: {}", orgName);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding org: {}", orgName);
                    orgApiClient.create(MappingUtil.map(org, Org.class));
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyApis(ManagementApiVersion serverVersion, List<DeclarativeApi> apis, String orgName) {
        LOGGER.debug("Applying APIs");

        apis.forEach(declarativeApi -> {
            final VersionAgnosticApi apiClient = managementApiService.buildServerApiClient(VersionAgnosticApi.class, serverVersion);
            final String apiName = declarativeApi.getName();

            // determine the version of the API being configured
            ofNullable(declarativeApi.getInitialVersion()).ifPresent(v ->
                    LOGGER.warn("Use of 'initialVersion' is deprecated and will be removed in future - use 'version' instead."));

            final String apiVersion = ofNullable(declarativeApi.getVersion()).orElse(declarativeApi.getInitialVersion());

            // create and configure API
            applyApi(serverVersion, apiClient, declarativeApi, orgName, apiName, apiVersion);

            // add policies
            applyPolicies(serverVersion, declarativeApi, orgName, apiName, apiVersion);

            // publish API
            if (declarativeApi.isPublished()) {
                apiService.publish(serverVersion, orgName, apiName, apiVersion);
            }
        });
    }

    /**
     * Add the API, if it is not present, then configure it.
     *
     * @param apiClient
     * @param declarativeApi
     * @param orgName
     * @param apiName
     * @param apiVersion
     * @return the state of the API
     */
    private void applyApi(ManagementApiVersion serverVersion, VersionAgnosticApi apiClient,
                          DeclarativeApi declarativeApi, String orgName, String apiName, String apiVersion) {

        LOGGER.debug("Applying API: {}", apiName);

        // base API
        of(ManagementApiUtil.checkExists(() -> apiClient.fetch(orgName, apiName)))
                .ifPresent(existing -> {
                    LOGGER.info("API '{}' already exists", apiName);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding '{}' API", apiName);
                    final Api api = MappingUtil.map(declarativeApi, Api.class);

                    // IMPORTANT: don't include version in the creation request
                    api.setInitialVersion(null);
                    api.setVersion(null);

                    // create API *without* version
                    apiClient.create(orgName, api);
                });

        // API version
        of(ManagementApiUtil.checkExists(() -> apiClient.fetchVersion(orgName, apiName, apiVersion)))
                .ifPresent(existing -> {
                    LOGGER.info("API '{}' version '{}' already exists", apiName, apiVersion);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding API '{}' version '{}'", apiName, apiVersion);

                    // create version
                    final ApiVersion apiVersionWrapper = new ApiVersion(apiVersion);
                    apiClient.createVersion(orgName, apiName, apiVersionWrapper);

                    if (v11x.equals(serverVersion)) {
                        // do this only on initial creation as v1.1.x API throws a 409 if this is called more than once
                        configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
                    }
                });

        if (v12x.equals(serverVersion)) {
            // The v1.2.x API supports configuration of the API even if published (but not retired)
            final String apiState = apiService.fetchCurrentState(serverVersion, orgName, apiName, apiVersion);
            if (ApiService.STATE_RETIRED.equals(apiState.toUpperCase())) {
                LOGGER.warn("API '{}' is retired - skipping configuration", apiName);

            } else {
                configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
            }
        }
    }

    /**
     * Configures the API using the declarative API configuration.
     *
     * @param declarativeApi
     * @param apiClient
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void configureApi(DeclarativeApi declarativeApi, VersionAgnosticApi apiClient,
                              String orgName, String apiName, String apiVersion) {

        LOGGER.info("Configuring API: {}", apiName);

        final ApiConfig apiConfig = MappingUtil.map(declarativeApi.getConfig(), ApiConfig.class);

        // map security configuration to endpoint properties
        ofNullable(declarativeApi.getConfig().getSecurity())
                .ifPresent(securityConfig -> apiConfig.setEndpointProperties(
                        MappingUtil.map(securityConfig, EndpointProperties.class)));

        apiClient.configure(orgName, apiName, apiVersion, apiConfig);
    }

    /**
     * Add policies to the API if they are not present.
     *
     * @param declarativeApi
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void applyPolicies(ManagementApiVersion serverVersion, DeclarativeApi declarativeApi,
                               String orgName, String apiName, String apiVersion) {

        ofNullable(declarativeApi.getPolicies()).ifPresent(declarativePolicies -> {
            LOGGER.debug("Applying policies to API: {}", apiName);

            // existing policies for the API
            final List<ApiPolicy> apiPolicies = policyService.fetchPolicies(serverVersion, orgName, apiName, apiVersion);

            declarativePolicies.forEach(declarativePolicy -> {
                final String policyName = declarativePolicy.getName();

                final ApiPolicy apiPolicy = new ApiPolicy(
                        MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));

                policyService.applyPolicies(serverVersion, orgName, apiName, apiVersion, apiPolicies, policyName, apiPolicy);
            });
        });
    }
}
