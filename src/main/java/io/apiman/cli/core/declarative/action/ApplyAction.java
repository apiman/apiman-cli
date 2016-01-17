/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.core.declarative.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.core.api.ServiceApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.api.model.ApiGateway;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.core.api.model.ServiceConfig;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.core.common.model.ServerAction;
import io.apiman.cli.core.declarative.model.Declaration;
import io.apiman.cli.core.declarative.model.DeclarativeApi;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.org.model.Org;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import retrofit.RetrofitError;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static io.apiman.cli.util.OptionalConsumer.of;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Applies a declaration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApplyAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(ApplyAction.class);
    private static final String JSON_EXTENSION = ".json";
    private static final String STATE_READY = "READY";
    private static final String STATE_PUBLISHED = "PUBLISHED";

    @Option(name = "--declarationFile", aliases = {"-f"}, usage = "Declaration file")
    private Path declarationFile;

    @Option(name = "-P", usage = "Set property (key=value)")
    private List<String> properties;

    @Override
    protected String getActionName() {
        return "Apply declaration";
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        final Declaration declaration;

        // parse declaration
        if (declarationFile.endsWith(JSON_EXTENSION)) {
            declaration = loadDeclaration(declarationFile, MappingUtil.JSON_MAPPER);
        } else {
            // default is YAML
            declaration = loadDeclaration(declarationFile, MappingUtil.YAML_MAPPER);
        }

        LOGGER.info("Loaded declaration: {}", declarationFile);
        LOGGER.debug("Declaration loaded: {}", () -> MappingUtil.safeWriteValueAsJson(declaration));

        try {
            applyDeclaration(declaration);
        } catch (Exception e) {
            throw new ActionException("Error applying declaration", e);
        }
    }

    /**
     * Apply the given Declaration.
     *
     * @param declaration the Declaration to apply.
     */
    public void applyDeclaration(Declaration declaration) {
        LOGGER.debug("Applying declaration");

        // add gateways
        applyGateways(declaration);

        // add plugins
        applyPlugins(declaration);

        // add org
        ofNullable(declaration.getOrg()).ifPresent(declarativeOrg -> {
            final String orgName = declaration.getOrg().getName();
            final OrgApi orgApiClient = buildApiClient(OrgApi.class);

            of(checkExists(() -> orgApiClient.fetch(orgName)))
                    .ifPresent(existing -> {
                        LOGGER.info("Org already exists: {}", orgName);
                    })
                    .ifNotPresent(() -> {
                        LOGGER.info("Adding org: {}", orgName);
                        orgApiClient.create(copy(declaration.getOrg(), Org.class));
                    });

            // add apis
            applyApis(declaration, orgName);
        });

        LOGGER.info("Applied declaration");
    }

    /**
     * Add gateways if they are not present.
     *
     * @param declaration
     */
    private void applyGateways(Declaration declaration) {
        ofNullable(declaration.getSystem().getGateways()).ifPresent(gateways -> {
            LOGGER.debug("Applying gateways");

            gateways.forEach(declarativeGateway -> {
                final GatewayApi apiClient = buildApiClient(GatewayApi.class);
                final String gatewayName = declarativeGateway.getName();

                of(checkExists(() -> apiClient.fetch(gatewayName)))
                        .ifPresent(existing -> {
                            LOGGER.info("Gateway already exists: {}", gatewayName);
                        })
                        .ifNotPresent(() -> {
                            LOGGER.info("Adding gateway: {}", gatewayName);

                            final Gateway gateway = copy(declarativeGateway, Gateway.class);
                            gateway.setConfiguration(MappingUtil.safeWriteValueAsJson(declarativeGateway.getConfig()));
                            apiClient.create(gateway);
                        });
            });
        });
    }

    /**
     * Add plugins if they are not present.
     *
     * @param declaration
     */
    private void applyPlugins(Declaration declaration) {
        ofNullable(declaration.getSystem().getPlugins()).ifPresent(plugins -> {
            LOGGER.debug("Applying plugins");

            plugins.forEach(plugin -> {
                final PluginApi apiClient = buildApiClient(PluginApi.class);

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
        return checkExists(apiClient::list)
                .map(apiPolicies -> apiPolicies.stream()
                        .anyMatch(installedPlugin ->
                                plugin.getArtifactId().equals(installedPlugin.getArtifactId()) &&
                                        plugin.getGroupId().equals(installedPlugin.getGroupId()) &&
                                        plugin.getVersion().equals(installedPlugin.getVersion()) &&
                                        safeEquals(plugin.getClassifier(), installedPlugin.getClassifier())
                        ))
                .orElse(false);
    }

    /**
     * Whether two nullable objects are equal.
     *
     * @param o1
     * @param o2
     * @return <code>true</code> if objects are equal, otherwise <code>false</code>
     */
    private <T> boolean safeEquals(T o1, T o2) {
        return (null == o1 && null == o2) ||
                ofNullable(o1).filter(o -> o.equals(o2)).isPresent();
    }

    /**
     * Add and configure APIs if they are not present.
     *
     * @param declaration
     * @param orgName
     */
    private void applyApis(Declaration declaration, String orgName) {
        ofNullable(declaration.getOrg().getApis()).ifPresent(declarativeApis -> {
            LOGGER.debug("Applying APIs");

            declarativeApis.forEach(declarativeApi -> {
                final ServiceApi apiClient = buildApiClient(ServiceApi.class);
                final String apiName = declarativeApi.getName();
                final String apiVersion = declarativeApi.getInitialVersion();

                // create and configure API
                applyApi(orgName, declarativeApi, apiClient, apiName, apiVersion);

                // add policies
                applyPolicies(orgName, declarativeApi, apiClient, apiName, apiVersion);

                // publish API
                if (declarativeApi.isPublished()) {
                    publish(orgName, apiClient, apiName, apiVersion);
                }
            });
        });
    }

    /**
     * Add and configure the API if it is not present.
     *
     * @param orgName
     * @param declarativeApi
     * @param apiClient
     * @param apiName
     * @param apiVersion
     */
    private void applyApi(String orgName, DeclarativeApi declarativeApi, ServiceApi apiClient, String apiName, String apiVersion) {
        LOGGER.debug("Applying API: {}", apiName);

        of(checkExists(() -> apiClient.fetch(orgName, apiName, apiVersion)))
                .ifPresent(existing -> {
                    LOGGER.info("API already exists: {}", apiName);
                })
                .ifNotPresent(() -> {
                    LOGGER.info("Adding API: {}", apiName);

                    // create API
                    final Api api = copy(declarativeApi, Api.class);
                    apiClient.create(orgName, api);

                    // configure API
                    // TODO move this outside of the block - currently API throws a 409 if this has been called
                    LOGGER.info("Configuring API: {}", apiName);
                    final ServiceConfig apiConfig = copy(declarativeApi.getConfig(), ServiceConfig.class);
                    apiConfig.setGateways(Lists.newArrayList(new ApiGateway(declarativeApi.getConfig().getGateway())));
                    apiClient.configure(orgName, apiName, apiVersion, apiConfig);
                });
    }

    /**
     * Add policies to the API if they are not present.
     *
     * @param orgName
     * @param declarativeApi
     * @param apiClient
     * @param apiName
     * @param apiVersion
     */
    private void applyPolicies(String orgName, DeclarativeApi declarativeApi, ServiceApi apiClient, String apiName, String apiVersion) {
        ofNullable(declarativeApi.getPolicies()).ifPresent(declarativePolicies -> {
            LOGGER.debug("Applying policies to API: {}", apiName);

            declarativePolicies.forEach(declarativePolicy -> {
                final String policyName = declarativePolicy.getName();

                // determine if the policy already exists for this API
                if (checkPolicyExists(orgName, apiClient, apiName, apiVersion, policyName)) {
                    LOGGER.info("Policy '{}' already exists for API: {}", policyName, apiName);

                } else {
                    LOGGER.info("Adding policy '{}' to API: {}", policyName, apiName);

                    // add policy
                    final ApiPolicy apiPolicy = new ApiPolicy(
                            policyName,
                            MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));

                    apiClient.addPolicy(orgName, apiName, apiVersion, apiPolicy);
                }
            });
        });
    }

    /**
     * Determine if the policy exists on the given API.
     *
     * @param orgName
     * @param apiClient
     * @param apiName
     * @param apiVersion
     * @param policyName
     * @return <code>true</code> if the policy exists on the API, otherwise <code>false</code>
     */
    private boolean checkPolicyExists(String orgName, ServiceApi apiClient, String apiName, String apiVersion, String policyName) {
        return checkExists(() ->
                apiClient.fetchPolicies(orgName, apiName, apiVersion))
                .map(apiPolicies -> apiPolicies.stream().anyMatch(apiPolicy ->
                        policyName.equals(apiPolicy.getPolicyDefinitionId())))
                .orElse(false);
    }

    /**
     * Publish the API, if it is in the 'Ready' state.
     *
     * @param orgName
     * @param apiClient
     * @param apiName
     * @param apiVersion
     */
    private void publish(String orgName, ServiceApi apiClient, String apiName, String apiVersion) {
        LOGGER.debug("Attempting to publish API: {}", apiName);

        final String apiState = ofNullable(apiClient.fetch(orgName, apiName, apiVersion).getStatus()).orElse("");
        switch (apiState.toUpperCase()) {
            case STATE_READY:
                performPublish(orgName, apiName, apiVersion);
                break;

            case STATE_PUBLISHED:
                LOGGER.info("API already published: {}", apiName);
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

        final ServerAction serverAction = new ServerAction(
                "publishService",
                orgName,
                apiName,
                apiVersion);

        buildApiClient(ActionApi.class).doAction(serverAction);
    }

    /**
     * Check for the presence of an item using the given Supplier.
     *
     * @param supplier the Supplier of the item
     * @param <T>
     * @return the item or {@link Optional#empty()}
     */
    private <T> Optional<T> checkExists(Supplier<T> supplier) {
        try {
            // attempt to return the item
            return ofNullable(supplier.get());

        } catch (RetrofitError re) {
            // 404 indicates the item does not exist - anything else is an error
            if (ofNullable(re.getResponse())
                    .filter(response -> HttpURLConnection.HTTP_NOT_FOUND == response.getStatus())
                    .isPresent()) {

                return empty();
            }

            throw new DeclarativeException("Error checking for existence of existing item", re);
        }
    }

    private <D, O> D copy(O original, Class<D> destinationClass) {
        try {
            final D destination = destinationClass.newInstance();

            final ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

            modelMapper.map(original, destination);

            return destination;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new DeclarativeException(e);
        }
    }

    public Declaration loadDeclaration(Path path, ObjectMapper mapper) {
        try (InputStream is = Files.newInputStream(path)) {
            String fileContents = CharStreams.toString(new InputStreamReader(is));
            LOGGER.trace("Declaration file raw: {}", fileContents);

            fileContents = MappingUtil.resolvePlaceholders(fileContents, properties);
            LOGGER.trace("Declaration file after resolving placeholders: {}", fileContents);

            return mapper.readValue(fileContents, Declaration.class);

        } catch (IOException e) {
            throw new DeclarativeException(e);
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
