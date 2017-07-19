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

import static java.util.Optional.ofNullable;

import io.apiman.cli.core.api.GatewayApi;
import io.apiman.cli.core.api.model.ApiGateway;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.core.declarative.model.DeclarativeApi;
import io.apiman.cli.core.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.core.declarative.model.DeclarativeGateway;
import io.apiman.cli.core.declarative.model.DeclarativePolicy;
import io.apiman.cli.core.gateway.model.GatewayConfig;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.management.ManagementApiUtil;
import io.apiman.cli.util.DeclarativeUtil;
import io.apiman.cli.util.MappingUtil;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Client;
import io.apiman.gateway.engine.beans.Policy;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;
import io.apiman.manager.api.core.plugin.AbstractPluginRegistry;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GatewayApplyCommand extends AbstractApplyCommand {

    private static final Logger LOGGER = LogManager.getLogger(GatewayApplyCommand.class);
    private static final String JSON_EXTENSION = ".json";

    private Map<String, Api> apiMap;

//    private Map<Api, List<DeclarativeGateway>> publishToGateway = new LinkedHashMap<>();

    private Map<String, Client> clientMap = new LinkedHashMap<>(); // TODO

    private String orgId;

    private Map<String, DeclarativeGateway> gatewaysMap = new LinkedHashMap<>();
    private Map<String, Plugin> pluginMap;



//    private Map<String, Plugin> pluginsByName = new LinkedHashMap<>();
//    private Map<Long, Plugin> pluginsById = new LinkedHashMap<>();

    @Override
    protected String getCommandDescription() {
        return "Apply gateway declaration";
    }

    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        orgId = declaration.getOrg().getName();
        gatewaysMap = declaration.getSystem().getGateways().stream().collect(Collectors.toMap(gw -> gw.getName(), gw -> gw));
        pluginMap = buildPluginMap(declaration);

        initialiseApis(declaration);

        publishAll();
    }

    private void publishAll() {
        publishToGateway.entrySet().forEach(entry -> {
            List<DeclarativeGateway> publishTo = entry.getValue();
            Api api = entry.getKey();

            publishTo.forEach(gateway -> publishApi(api, gateway));
        });
    }

    private void publishApi(Api api, DeclarativeGateway gateway) {
        GatewayConfig config = gateway.getConfig();
        GatewayApi client = buildGatewayApiClient(config.getEndpoint(),
                config.getUsername(),
                config.getPassword(),
                getLogDebug());

        LOGGER.fatal("Publishing {} to {}", api, gateway.getConfig().getEndpoint());
        client.publishApi(api);
    }

    private void initialiseApis(BaseDeclaration declaration) {
        ofNullable(declaration.getOrg().getApis()).map(apis -> {
            apis.stream().collect(Collectors.toMap(this::getKey, this::initiliseApi));
        });
    }

    private Api initiliseApi(DeclarativeApi modelApi) {
        Api api = new Api();
        api.setOrganizationId(orgId);
        api.setApiId(modelApi.getName());
        api.setVersion(modelApi.getVersion());

        DeclarativeApiConfig apiConfig = modelApi.getConfig();
        api.setEndpoint(apiConfig.getEndpoint());
        api.setPublicAPI(apiConfig.isMakePublic()); // Why is this different to publicApi?
        api.setEndpointType(apiConfig.getEndpointType());
        // api.setEndpointProperties(apiConfig.getEndpointProperties().); TODO flatten into map, maybe turn manager aspects into library? danger of drifting.
        // api.setParsePayload(apiConfig.getEndpointProperties().); TODO how is this determined/stored?

        // Create empty sublist for if not present.
        publishToGateway.computeIfAbsent(api, isAbsent -> {
            return new ArrayList<>();
        });

        // Use singular gatewayId or list of gateways
        List<String> gatewayIds = ofNullable(apiConfig.getGateways())
                .orElse(Arrays.asList(new ApiGateway(apiConfig.getGateway()))) // Hmm
                .stream()
                .map(ApiGateway::getGatewayId)
                .collect(Collectors.toList());

        // Bad name: Add to the map of APIs to be published on a given gateway.
        publishApiOnGateway(api, gatewayIds); // TODO

        //System.out.println("Prepared API... "  + api);

        //processPolicies(api, modelApi.getPolicies());
        api.setApiPolicies(buildPolicyChain(modelApi.getPolicies()));

        return api;
    }



    private List<Policy> buildPolicyChain(List<DeclarativePolicy> policies) {
        return policies.stream().map(declarativePolicy -> {
            Policy policy = new Policy();
            policy.setPolicyImpl(determinePolicyImpl(declarativePolicy));
            policy.setPolicyJsonConfig(MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));
            return policy;
        }).collect(Collectors.toList());
    }

    private String determinePolicyImpl(DeclarativePolicy declarativePolicy) {
        if (declarativePolicy.isPlugin()) {
            LOGGER.info("Resolving plugin: {}", declarativePolicy.getPlugin());
            // Get plugin reference first
            Plugin plugin = ofNullable(pluginMap.get(declarativePolicy.getPlugin()))
                .orElseThrow(() -> new IllegalArgumentException("No such plugin exists " + declarativePolicy.getPlugin()));

            // We need the FQCN for the end of our policy URI. To determine it from *-policyDef.json
            // * First, download the plugin and inspect the metadata.
            // * If only a single policy implementation exists, just use it.
            // * If multiple policy implementations exist then `name` *must* be provided to disambiguate.

            try {
                io.apiman.common.plugin.Plugin apimanPlugin = apimanPluginRegistry.loadPlugin(plugin.getCoordinates());
                List<PolicyDefinitionBean> policyDefs = apimanPlugin.getPolicyDefinitions().stream()
                        .map(url -> MappingUtil.readJsonValue(url, PolicyDefinitionBean.class))
                        .collect(Collectors.toList()); //TODO see PluginResourceImpl L189 extract common validation aspects

                LOGGER.info("Plugin contains {} policy definitions", policyDefs.size());
                PolicyDefinitionBean selected = null;

                if (policyDefs.isEmpty()) {
                    throw new RuntimeException("Plugin contained no policies");
                } else if (policyDefs.size() == 1) {
                    selected = policyDefs.get(0);
                    LOGGER.info("Automatically selecting policy: {}", selected);
                } else {
                    String name = ofNullable(declarativePolicy.getName()) // TODO or Id
                            .orElseThrow(() -> new RuntimeException("Multiple policyDefs exist in plugin. You must disambiguate by providing its name. Run the command FOO BAR BAZ show available policies."));

                    selected = policyDefs.stream()
                            .filter(def -> name.equals(def.getId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Plugin did not contain the indicated policy ")); // TODO

                    LOGGER.info("Selecting policy {} ({})", selected.getId(), selected.getName());
                }
                LOGGER.debug("Selected policyDef {}", selected);
                return selected.getPolicyImpl();
                // TODO print out useful log info
            } catch (InvalidPluginException e) {
                LOGGER.fatal("Plugin {} could not be found {}", plugin.getCoordinates(), e);
                throw new RuntimeException(e); // TODO must be a more accurate exception somewhere
            }
        } else {
            LOGGER.error("Loading inbuilt policy: {}", declarativePolicy.getName());
            // TODO Look up in baked map rather than guessing!
            return "class:io.apiman.gateway.engine.policies." + declarativePolicy.getName();
        }
    }

    // TODO bad name, just builds the map
    private void publishApiOnGateway(Api api, List<String> gatewayIds) {
        gatewayIds.forEach(id -> {
            // Look up gateway by ID
            DeclarativeGateway gw = gatewaysMap.get(id);
            // For given API publish to indicated GW
            publishToGateway.get(api).add(gw);
        });
    }

    private String getKey(DeclarativeApi modelApi) {
        return modelApi.getName() + "-" + modelApi.getVersion(); //$NON-NLS-1$
    }

    private Map<String, Plugin> buildPluginMap(BaseDeclaration declaration) {
        LOGGER.info("Building plugin map...");
        return declaration.getSystem().getPlugins().stream()
                .collect(Collectors.toMap(this::determineName, p -> p));
    }

    private String determineName(Plugin plugin) {
        if (plugin.getName() == null) {
            LOGGER.info("A plugin has been specified without a friendly name. References must be by its full coordinates: {}", plugin.getCoordinates());
            return plugin.getCoordinates().toString();
        } else {
            return plugin.getName();
        }
    }

    private void buildClients(BaseDeclaration declaration) {

    }

    private GatewayApi buildGatewayApiClient(String endpoint, String username, String password, boolean debugLogging) {
        return ManagementApiUtil.buildGatewayApiClient(
                endpoint,
                username,
                password,
                debugLogging);
    }

    @Override
    protected BaseDeclaration loadDeclaration(Path declarationFile, Map<String, String> parsedProperties) {
        // parse declaration
        if (declarationFile.endsWith(JSON_EXTENSION)) {
            return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.JSON_MAPPER, parsedProperties);
        } else {
            // default is YAML
            return DeclarativeUtil.loadDeclaration(declarationFile, MappingUtil.YAML_MAPPER, parsedProperties);
        }
    }

    private final ApimanPluginRegistry apimanPluginRegistry = new ApimanPluginRegistry();

    private static final class ApimanPluginRegistry extends AbstractPluginRegistry {
        ApimanPluginRegistry() {
            super(new File("/tmp/pluginDir"));
        }
    }
}
