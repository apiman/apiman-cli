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

import com.google.common.io.CharStreams;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.EndpointProperties;
import io.apiman.cli.command.api.model.EntityVersion;
import io.apiman.cli.command.client.model.Client;
import io.apiman.cli.command.declarative.model.DeclarativeApi;
import io.apiman.cli.command.declarative.model.DeclarativeClient;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.command.declarative.model.DeclarativeOrg;
import io.apiman.cli.command.declarative.model.DeclarativePlan;
import io.apiman.cli.command.declarative.model.DeclarativePolicy;
import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.command.org.model.Org;
import io.apiman.cli.command.plan.model.Plan;
import io.apiman.cli.command.plan.model.PlanVersion;
import io.apiman.cli.managerapi.command.api.PolicyApi;
import io.apiman.cli.managerapi.command.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.command.gateway.GatewayApi;
import io.apiman.cli.managerapi.command.org.OrgApi;
import io.apiman.cli.managerapi.command.plan.PlanApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.managerapi.service.delegates.ClientPolicyDelegate;
import io.apiman.cli.managerapi.service.delegates.PlanPolicyDelegate;
import io.apiman.cli.util.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.mime.TypedString;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static io.apiman.cli.managerapi.command.common.model.ManagementApiVersion.v11x;
import static io.apiman.cli.managerapi.command.common.model.ManagementApiVersion.v12x;
import static io.apiman.cli.util.Functions.of;
import static java.util.Optional.ofNullable;

/**
 * Applies changes in a declarative fashion.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeServiceImpl implements DeclarativeService {
    private static final Logger LOGGER = LogManager.getLogger(DeclarativeServiceImpl.class);

    private final ManagementApiService managementApiService;
    private final ClientService clientService;
    private final ApiService apiService;
    private final PlanService planService;
    private final PolicyService policyService;

    @Inject
    public DeclarativeServiceImpl(ManagementApiService managementApiService,
                                  ClientService clientService,
                                  ApiService apiService,
                                  PlanService planService,
                                  PolicyService policyService) {

        this.managementApiService = managementApiService;
        this.clientService = clientService;
        this.apiService = apiService;
        this.planService = planService;
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

            // add definition
            applyDefinition(apiClient, declarativeApi, orgName, apiName, apiVersion);

            // add policies
            applyApiPolicies(apiClient, serverVersion, declarativeApi, orgName, apiName, apiVersion);

            // publish API
            if (declarativeApi.isPublished()) {
                apiService.publish(serverVersion, orgName, apiName, apiVersion);
            }
        });
    }

    @Override
    public void applyClients(ManagementApiVersion serverVersion, List<DeclarativeClient> clients, String orgName) {
        LOGGER.debug("Applying Clients");

        clients.forEach(declarativeClient -> {
            final ClientApi clientApi = managementApiService.buildServerApiClient(ClientApi.class, serverVersion);
            final String clientName = declarativeClient.getName();

            // determine the version of the API being configured
            ofNullable(declarativeClient.getInitialVersion()).ifPresent(v ->
                    LOGGER.warn("Use of 'initialVersion' is deprecated and will be removed in future - use 'version' instead."));

            final String apiVersion = ofNullable(declarativeClient.getVersion()).orElse(declarativeClient.getInitialVersion());

            // create and configure Client
            applyClient(clientApi, declarativeClient, orgName, clientName, apiVersion);

            // add policies
            applyClientPolicies(ClientPolicyDelegate.wrap(clientApi), serverVersion, declarativeClient, orgName, clientName, apiVersion);

            // Register Client
            if (declarativeClient.isRegistered()) {
                clientService.register(serverVersion, orgName, clientName, apiVersion);
            }
        });
    }

    @Override
    public void applyPlans(ManagementApiVersion serverVersion, List<DeclarativePlan> plans, String orgName) {
        LOGGER.debug("Applying Plans");

        plans.forEach(declarativePlan -> {
            final PlanApi planApi = managementApiService.buildServerApiClient(PlanApi.class);
            final String planName = declarativePlan.getName();

            // determine the version of the API being configured
            ofNullable(declarativePlan.getInitialVersion()).ifPresent(v ->
                    LOGGER.warn("Use of 'initialVersion' is deprecated and will be removed in future - use 'version' instead."));

            final String planVersion = ofNullable(declarativePlan.getVersion()).orElse(declarativePlan.getInitialVersion());

            // create and configure API
            applyPlan(planApi, declarativePlan, orgName, planName, planVersion);

            // add policies
            applyPlanPolicies(PlanPolicyDelegate.wrap(planApi), serverVersion, declarativePlan, orgName, planName, planVersion);

            // lock plan
            if (declarativePlan.isLocked()) {
                planService.lock(orgName, planName, planVersion);
            }
        });
    }

    /**
     * Add the API, if it is not present, then configure it.
     *
     * @param apiClient
     * @param declarativeClient
     * @param orgName
     * @param clientName
     * @param clientVersion
     * @return the state of the API
     */
    private void applyClient(ClientApi apiClient,
                             DeclarativeClient declarativeClient, String orgName, String clientName, String clientVersion) {

        LOGGER.debug("Applying Client: {}", clientName);

        // base Client
        of(ManagementApiUtil.checkExists(() -> apiClient.fetch(orgName, clientName)))
                .ifPresent(existing -> {
                    LOGGER.info("Client '{}' already exists", clientName);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding '{}' Client", clientName);
                    final Client client = MappingUtil.map(declarativeClient, Client.class);

                    // IMPORTANT: don't include version in the creation request
                    client.setInitialVersion(null);
                    client.setVersion(null);

                    // create Client *without* version
                    apiClient.create(orgName, client);
                });

        // Client version
        of(ManagementApiUtil.checkExists(() -> apiClient.fetchVersion(orgName, clientName, clientVersion)))
                .ifPresent(existing -> {
                    LOGGER.info("Client '{}' version '{}' already exists", clientName, clientVersion);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding Client '{}' version '{}'", clientName, clientVersion);

                    // create version
                    final EntityVersion apiVersionWrapper = new EntityVersion(clientVersion);
                    Client client = apiClient.createVersion(orgName, clientName, apiVersionWrapper);

                    // Apply contracts
                    applyContracts(apiClient, orgName, client);
                });
    }

    private void applyContracts(ClientApi api, String orgName, Client client) {
        ofNullable(client.getContracts()).ifPresent(contracts -> {
            LOGGER.debug("Applying contracts to Client: {}", client.getName());

            contracts.forEach(contract -> {
                api.createContract(orgName, client.getVersion(), client.getVersion(), contract);
            });
        });
    }

    private void applyPlan(PlanApi planClient,
                             DeclarativePlan declarativeClient, String orgName, String clientName, String clientVersion) {

        LOGGER.debug("Applying Plan: {}", clientName);

        // base API
        of(ManagementApiUtil.checkExists(() -> planClient.fetch(orgName, clientName)))
                .ifPresent(existing -> {
                    LOGGER.info("Plan '{}' already exists", clientName);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding '{}' Plan", clientName);
                    final Plan client = MappingUtil.map(declarativeClient, Plan.class);

                    // IMPORTANT: don't include version in the creation request
                    client.setInitialVersion(null);
                    client.setVersion(null);

                    // create API *without* version
                    planClient.create(orgName, client);
                });

        // API version
        of(ManagementApiUtil.checkExists(() -> planClient.fetchVersion(orgName, clientName, clientVersion)))
                .ifPresent(existing -> {
                    LOGGER.info("Plan '{}' version '{}' already exists", clientName, clientVersion);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding Plan '{}' version '{}'", clientName, clientVersion);

                    // create version
                    final PlanVersion planVersion = new PlanVersion(clientVersion);
                    planClient.createVersion(orgName, clientName, planVersion);
                });
    }

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
                    final EntityVersion apiVersionWrapper = new EntityVersion(apiVersion);
                    apiClient.createVersion(orgName, apiName, apiVersionWrapper);

                    if (v11x.equals(serverVersion)) {
                        // do this only on initial creation as v1.1.x API throws a 409 if this is called more than once
                        configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
                    }
                });

        if (v12x.equals(serverVersion)) {
            // The v1.2.x API supports configuration of the API even if published (but not retired)
            // but only if a public API
            final String apiState = apiService.fetchCurrentState(serverVersion, orgName, apiName, apiVersion);

            // If retired, then skip
            if (ApiService.STATE_RETIRED.equalsIgnoreCase(apiState)) {
                LOGGER.warn("API '{}' is retired - skipping configuration", apiName);
            } else {
                // If it's a public API or any API in ready state, then it's safe to configure.
                if (declarativeApi.getConfig().isPublicApi() || apiService.isUnpublished(apiState)) {
                    configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
                } else {
                    // Otherwise it's a private API and in a non-modifiable state.
                    LOGGER.warn("API '{}' is not in a modifiable state {} - skipping configuration", apiName, apiState);
                }
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
     * Adds a definition to the API.
     */
    private void applyDefinition(VersionAgnosticApi apiClient, DeclarativeApi declarativeApi, String orgName,
                                 String apiName, String apiVersion) {

        ofNullable(declarativeApi.getDefinition()).ifPresent(declarativeApiDefinition -> {
            if (StringUtils.isNotEmpty(declarativeApiDefinition.getFile())
                    || StringUtils.isNotEmpty(declarativeApiDefinition.getBody())) {

                LOGGER.debug("Applying definition to API: {}", apiName);
                final String definition;
                if (StringUtils.isNotEmpty(declarativeApiDefinition.getFile())) {
                    try (InputStream is = Files.newInputStream(Paths.get(declarativeApiDefinition.getFile()), StandardOpenOption.READ)) {
                        definition = CharStreams.toString(new InputStreamReader(is));
                    } catch (IOException e) {
                        LOGGER.error("Failed to apply API definition, invalid file: {}", declarativeApiDefinition.getFile(), e);
                        return;
                    }
                } else {
                    definition = declarativeApiDefinition.getBody();
                }

                apiClient.setDefinition(orgName, apiName, apiVersion, declarativeApiDefinition.getType(), new TypedString(definition));

                LOGGER.info("Setting definition for API: {}", apiName);
            }
        });
    }

    private void applyPlanPolicies(PolicyApi policyApi,
                                  ManagementApiVersion serverVersion,
                                  DeclarativePlan declarativePlan,
                                  String orgName,
                                  String apiName,
                                  String apiVersion) {

        applyPolicies(policyApi, serverVersion, orgName, apiName, apiVersion, declarativePlan.getPolicies());
    }

    private void applyApiPolicies(PolicyApi policyApi,
                                  ManagementApiVersion serverVersion,
                                  DeclarativeApi declarativeApi,
                                  String orgName,
                                  String apiName,
                                  String apiVersion) {

        applyPolicies(policyApi, serverVersion, orgName, apiName, apiVersion, declarativeApi.getPolicies());
    }

    private void applyClientPolicies(PolicyApi policyApi,
                                     ManagementApiVersion serverVersion,
                                     DeclarativeClient declarativeClient,
                                     String orgName,
                                     String apiName,
                                     String apiVersion) {

        applyPolicies(policyApi, serverVersion, orgName, apiName, apiVersion, declarativeClient.getPolicies());
    }

    /**
     * Add policies to the API if they are not present.
     */
    private void applyPolicies(PolicyApi policyApi, ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion, List<DeclarativePolicy> policies) {
        ofNullable(policies).ifPresent(declarativePolicies -> {
            LOGGER.debug("Applying policies to Entity: {}", apiName);

            // existing policies for the API
            final List<ApiPolicy> apiPolicies = policyService.fetchPolicies(policyApi, serverVersion, orgName, apiName, apiVersion);

            declarativePolicies.forEach(declarativePolicy -> {
                final String policyName = declarativePolicy.getName();

                final ApiPolicy apiPolicy = new ApiPolicy(
                        MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));

                policyService.applyPolicies(policyApi, serverVersion, orgName, apiName, apiVersion, apiPolicies, policyName, apiPolicy);
            });
        });
    }
    
}
