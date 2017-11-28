/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.core.declarative.command;

import static io.apiman.cli.util.Functions.of;
import static java.util.Optional.ofNullable;

import io.apiman.cli.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.api.model.ApiConfig;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.core.api.model.ApiVersion;
import io.apiman.cli.core.api.model.EndpointProperties;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.core.common.model.ManagementApiVersion;
import io.apiman.cli.core.common.util.ServerActionUtil;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.core.declarative.model.DeclarativeApi;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.org.model.Org;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.BeanUtil;
import io.apiman.cli.util.DeclarativeUtil;
import io.apiman.cli.util.MappingUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

import com.google.common.io.CharStreams;

import retrofit.mime.TypedString;

public class ManagerApplyCommand extends AbstractApplyCommand {
    private static final String STATE_READY = "READY";
    private static final String STATE_PUBLISHED = "PUBLISHED";
    private static final String STATE_RETIRED = "RETIRED";
    private static final Logger LOGGER = LogManager.getLogger(ManagerApplyCommand.class);

    @Option(name = "--serverVersion", aliases = {"-sv"}, usage = "Management API server version")
    private ManagementApiVersion serverVersion = ManagementApiVersion.DEFAULT_VERSION;

    /**
     * Apply the given Declaration.
     *
     * @param declaration the Declaration to apply.
     */
    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        LOGGER.debug("Applying declaration");

        // add gateways
        applyGateways(declaration);

        // add plugins
        applyPlugins(declaration);

        // add org
        ofNullable(declaration.getOrg()).ifPresent(declarativeOrg -> {
            final String orgName = declaration.getOrg().getName();
            final OrgApi orgApiClient = buildServerApiClient(OrgApi.class);

            of(DeclarativeUtil.checkExists(() -> orgApiClient.fetch(orgName)))
                    .ifPresent(existing -> {
                        LOGGER.info("Org already exists: {}", orgName);
                    })
                    .ifNotPresent(() -> {
                        LOGGER.info("Adding org: {}", orgName);
                        orgApiClient.create(MappingUtil.map(declaration.getOrg(), Org.class));
                    });

            // add apis
            applyApis(declaration, orgName);
        });

        LOGGER.info("Applied declaration");
    }

    /**
     * Add gateways if they are not present.
     *
     * @param declaration the Declaration to apply.
     */
    private void applyGateways(BaseDeclaration declaration) {
        ofNullable(declaration.getSystem().getGateways()).ifPresent(gateways -> {
            LOGGER.debug("Applying gateways");

            gateways.forEach(declarativeGateway -> {
                final GatewayApi apiClient = buildServerApiClient(GatewayApi.class);
                final String gatewayName = declarativeGateway.getName();

                of(DeclarativeUtil.checkExists(() -> apiClient.fetch(gatewayName)))
                        .ifPresent(existing -> {
                            LOGGER.info("Gateway already exists: {}", gatewayName);
                        })
                        .ifNotPresent(() -> {
                            LOGGER.info("Adding gateway: {}", gatewayName);

                            final Gateway gateway = MappingUtil.map(declarativeGateway, Gateway.class);
                            apiClient.create(gateway);
                        });
            });
        });
    }

    /**
     * Add plugins if they are not present.
     *
     * @param declaration the Declaration to apply.
     */
    private void applyPlugins(BaseDeclaration declaration) {
        ofNullable(declaration.getSystem().getPlugins()).ifPresent(plugins -> {
            LOGGER.debug("Applying plugins");

            plugins.forEach(plugin -> {
                final PluginApi apiClient = buildServerApiClient(PluginApi.class);

                if (checkPluginExists(plugin, apiClient)) {
                    LOGGER.info("Plugin already installed: {}", plugin.getName());
                } else {
                    LOGGER.info("Installing plugin: {}", plugin.getName());
                    apiClient.create(plugin);
                }
            });
        });
    }

    /**
     * Determine if the plugin is installed.
     *
     * @param plugin
     * @param apiClient
     * @return <code>true</code> if the plugin is installed, otherwise <code>false</code>
     */
    private boolean checkPluginExists(Plugin plugin, PluginApi apiClient) {
        return DeclarativeUtil.checkExists(apiClient::list)
                .map(apiPolicies -> apiPolicies.stream()
                        .anyMatch(installedPlugin ->
                                plugin.getArtifactId().equals(installedPlugin.getArtifactId()) &&
                                        plugin.getGroupId().equals(installedPlugin.getGroupId()) &&
                                        plugin.getVersion().equals(installedPlugin.getVersion()) &&
                                        BeanUtil.safeEquals(plugin.getClassifier(), installedPlugin.getClassifier())
                        ))
                .orElse(false);
    }

    /**
     * Add and configure APIs if they are not present.
     *
     * @param declaration the Declaration to apply.
     * @param orgName
     */
    private void applyApis(BaseDeclaration declaration, String orgName) {
        ofNullable(declaration.getOrg().getApis()).ifPresent(declarativeApis -> {
            LOGGER.debug("Applying APIs");

            declarativeApis.forEach(declarativeApi -> {
                final VersionAgnosticApi apiClient = buildServerApiClient(VersionAgnosticApi.class, serverVersion);
                final String apiName = declarativeApi.getName();

                // determine the version of the API being configured
                ofNullable(declarativeApi.getInitialVersion()).ifPresent(v ->
                        LOGGER.warn("Use of 'initialVersion' is deprecated and will be removed in future - use 'version' instead."));

                final String apiVersion = ofNullable(declarativeApi.getVersion()).orElse(declarativeApi.getInitialVersion());

                // create and configure API
                applyApi(apiClient, declarativeApi, orgName, apiName, apiVersion);

                // add definition
                applyDefinition(apiClient, declarativeApi, orgName, apiName, apiVersion);

                // add policies
                applyPolicies(apiClient, declarativeApi, orgName, apiName, apiVersion);

                // publish API
                if (declarativeApi.isPublished()) {
                    publish(apiClient, orgName, apiName, apiVersion);
                }
            });
        });
    }

    /**
     * Add and configure the API if it is not present.
     *
     * @param apiClient
     * @param declarativeApi
     * @param orgName
     * @param apiName
     * @param apiVersion
     * @return the state of the API
     */
    private void applyApi(VersionAgnosticApi apiClient, DeclarativeApi declarativeApi, String orgName,
                          String apiName, String apiVersion) {

        LOGGER.debug("Applying API: {}", apiName);

        // base API
        of(DeclarativeUtil.checkExists(() -> apiClient.fetch(orgName, apiName)))
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
        of(DeclarativeUtil.checkExists(() -> apiClient.fetchVersion(orgName, apiName, apiVersion)))
                .ifPresent(existing -> {
                    LOGGER.info("API '{}' version '{}' already exists", apiName, apiVersion);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding API '{}' version '{}'", apiName, apiVersion);

                    // create version
                    final ApiVersion apiVersionWrapper = new ApiVersion(apiVersion);
                    apiClient.createVersion(orgName, apiName, apiVersionWrapper);

                    if (ManagementApiVersion.v11x.equals(serverVersion)) {
                        // do this only on initial creation as v1.1.x API throws a 409 if this is called more than once
                        configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
                    }
                });

        if (ManagementApiVersion.v12x.equals(serverVersion)) {
            // The v1.2.x API supports configuration of the API even if published (but not retired)
            final String apiState = fetchCurrentState(apiClient, orgName, apiName, apiVersion);
            if (STATE_RETIRED.equals(apiState.toUpperCase())) {
                LOGGER.warn("API '{}' is retired - skipping configuration", apiName);

            } else {
                configureApi(declarativeApi, apiClient, orgName, apiName, apiVersion);
            }
        }
    }

    /**
     * Return the current state of the API.
     *
     * @param apiClient
     * @param orgName
     * @param apiName
     * @param apiVersion
     * @return the API state
     */
    private String fetchCurrentState(VersionAgnosticApi apiClient, String orgName, String apiName, String apiVersion) {
        final String apiState = ofNullable(apiClient.fetchVersion(orgName, apiName, apiVersion).getStatus()).orElse("");
        LOGGER.debug("API '{}' state: {}", apiName, apiState);
        return apiState;
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
     * @param apiClient
     * @param declarativeApi
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void applyPolicies(VersionAgnosticApi apiClient, DeclarativeApi declarativeApi, String orgName,
                               String apiName, String apiVersion) {

        ofNullable(declarativeApi.getPolicies()).ifPresent(declarativePolicies -> {
            LOGGER.debug("Applying policies to API: {}", apiName);

            // existing policies for the API
            final List<ApiPolicy> apiPolicies = apiClient.fetchPolicies(orgName, apiName, apiVersion);

            declarativePolicies.forEach(declarativePolicy -> {
                final String policyName = declarativePolicy.getName();

                final ApiPolicy apiPolicy = new ApiPolicy(
                        MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));

                // determine if the policy already exists for this API
                final Optional<ApiPolicy> existingPolicy = apiPolicies.stream()
                        .filter(p -> policyName.equals(p.getPolicyDefinitionId()))
                        .findFirst();

                if (existingPolicy.isPresent()) {
                    if (ManagementApiVersion.v12x.equals(serverVersion)) {
                        // update the existing policy config
                        LOGGER.info("Updating existing policy '{}' configuration for API: {}", policyName, apiName);

                        final Long policyId = existingPolicy.get().getId();
                        apiClient.configurePolicy(orgName, apiName, apiVersion, policyId, apiPolicy);

                    } else {
                        LOGGER.info("Policy '{}' already exists for API '{}' - skipping configuration update", policyName, apiName);
                    }

                } else {
                    // add new policy
                    LOGGER.info("Adding policy '{}' to API: {}", policyName, apiName);

                    apiPolicy.setDefinitionId(policyName);
                    apiClient.addPolicy(orgName, apiName, apiVersion, apiPolicy);
                }
            });
        });
    }

    /**
     * Adds a definition to the API.
     *
     * @param apiClient
     * @param declarativeApi
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void applyDefinition(VersionAgnosticApi apiClient, DeclarativeApi declarativeApi, String orgName,
                                 String apiName, String apiVersion) {

        ofNullable(declarativeApi.getDefinition()).ifPresent(declarativeApiDefinition -> {

            if (StringUtils.isNotEmpty(declarativeApiDefinition.getFile())
                    || StringUtils.isNotEmpty(declarativeApiDefinition.getBody())) {
                LOGGER.debug("Applying definition to API: {}", apiName);
                String definition = "";
                if (StringUtils.isNotEmpty(declarativeApiDefinition.getFile())) {
                    try (InputStream is = Files.newInputStream(Paths.get(declarativeApiDefinition.getFile()), StandardOpenOption.READ)) {
                        definition = CharStreams.toString(new InputStreamReader(is));
                    } catch (IOException e) {
                        LOGGER.error("Failed to apply api definition, invalid file: " + declarativeApiDefinition.getFile(), e);
                    }
                } else {
                    definition = declarativeApiDefinition.getBody();
                }

                apiClient.setDefinition(orgName, apiName, apiVersion, declarativeApiDefinition.getType(), new TypedString(definition));

                LOGGER.info("Setting definition for API: {}", apiName);
            }

        });

    }

    /**
     * Publish the API, if it is in the 'Ready' state.
     *
     * @param apiClient
     * @param orgName
     * @param apiName
     * @param apiVersion
     */
    private void publish(VersionAgnosticApi apiClient, String orgName, String apiName, String apiVersion) {
        LOGGER.debug("Attempting to publish API: {}", apiName);
        final String apiState = fetchCurrentState(apiClient, orgName, apiName, apiVersion);

        switch (apiState.toUpperCase()) {
            case STATE_READY:
                performPublish(orgName, apiName, apiVersion);
                break;

            case STATE_PUBLISHED:
                switch (serverVersion) {
                    case v11x:
                        LOGGER.info("API '{}' already published - skipping republish", apiName);
                        break;

                    case v12x:
                        LOGGER.info("Republishing API: {}", apiName);
                        performPublish(orgName, apiName, apiVersion);
                        break;
                }
                break;

            default:
                throw new DeclarativeException(String.format(
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
    private void performPublish(String orgName, String apiName, String apiVersion) {
        LOGGER.info("Publishing API: {}", apiName);
        ServerActionUtil.publishApi(orgName, apiName, apiVersion, serverVersion, buildServerApiClient(ActionApi.class));
    }

    public void setServerVersion(ManagementApiVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
