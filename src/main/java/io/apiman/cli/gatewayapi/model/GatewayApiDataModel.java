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
package io.apiman.cli.gatewayapi.model;

import io.apiman.cli.command.api.model.ApiGateway;
import io.apiman.cli.command.api.model.EndpointProperties;
import io.apiman.cli.command.declarative.model.BaseDeclaration;
import io.apiman.cli.command.declarative.model.DeclarativeApi;
import io.apiman.cli.command.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.command.declarative.model.DeclarativePolicy;
import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.command.plugin.model.Plugin;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.util.MappingUtil;
import io.apiman.cli.util.PolicyResolver;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Policy;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Data model representing the API Gateway(s) - as opposed to the API Manager.
 *
 * Currently transforms from the declarative format.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayApiDataModel {
    private static final Logger LOGGER = LogManager.getLogger(GatewayApiDataModel.class);
    private final PolicyResolver policyResolver;
    private final BaseDeclaration declaration;

    private String orgId;
    private Map<String, DeclarativeGateway> gatewaysMap;
    private Map<String, Plugin> pluginMap;
    private Map<Api, List<DeclarativeGateway>> apiToGatewaysMap;

    private Map<DeclarativeGateway, List<Api>> gatewayToApisMap;

    public GatewayApiDataModel(BaseDeclaration declaration,
                               PolicyResolver policyResolver) {
        this.declaration = declaration;
        this.policyResolver = policyResolver;
        buildDataModel();
    }

    public Map<String, DeclarativeGateway> getGatewaysMap() {
        return gatewaysMap;
    }

    public Map<String, Plugin> getPluginMap() {
        return pluginMap;
    }

    public Map<Api, List<DeclarativeGateway>> getApiToGatewaysMap() {
        return apiToGatewaysMap;
    }

    public Map<DeclarativeGateway, List<Api>> getGatewayToApisMap() {
        return gatewayToApisMap;
    }

    private void buildDataModel() {
        orgId = declaration.getOrg().getName();
        LOGGER.debug("Organization ID: {}", orgId);

        gatewaysMap = declaration.getSystem().getGateways()
                .stream()
                .collect(Collectors.toMap(Gateway::getName, gw -> gw));

        LOGGER.debug("Gateways map: {}", gatewaysMap);

        pluginMap = buildPluginMap(declaration);
        LOGGER.debug("Plugin map: {}", pluginMap);

        apiToGatewaysMap = buildApisToGatewayMap(declaration);
        LOGGER.debug("APIs to Gateway map: {}", apiToGatewaysMap);

        gatewayToApisMap = buildApisOnGatewaysMap();
        LOGGER.debug("Gateways to APIs map: {}", apiToGatewaysMap);
    }

    private Map<DeclarativeGateway, List<Api>> buildApisOnGatewaysMap() {
        Map<DeclarativeGateway, List<Api>> outMap = new LinkedHashMap<>();
        // Look at API to Gateway Map and invert to be Gateway to API
        for(Map.Entry<Api, List<DeclarativeGateway>> entry : apiToGatewaysMap.entrySet()) {
            Api addApi = entry.getKey();
            List<DeclarativeGateway> gateways = entry.getValue();
            for (DeclarativeGateway gateway : gateways) {
                List<Api> apiList = outMap.getOrDefault(gateway, new ArrayList<>());
                apiList.add(addApi);
                outMap.put(gateway, apiList);
            }
        }
        return outMap;
    }

    private Map<String, Plugin> buildPluginMap(BaseDeclaration declaration) {
        Map<String, Plugin> pluginMap = new LinkedHashMap<>();
        List<Plugin> pluginsList = ofNullable(declaration.getSystem().getPlugins())
                .orElse(Collections.emptyList());

        pluginsList
                .forEach(plugin -> {
                    if (plugin.getName() == null) {
                        LOGGER.info("A plugin has been specified without a friendly name. " +
                                "References must be by its full coordinates: {}", plugin.getCoordinates());
                    } else {
                        pluginMap.put(plugin.getName(), plugin);
                    }
                    pluginMap.put(plugin.getCoordinates().toString(), plugin);
                });
        return pluginMap;
    }

    // Build map of APIs to Gateway that they should be published on.
    private Map<Api, List<DeclarativeGateway>> buildApisToGatewayMap(BaseDeclaration declaration) {
        return ofNullable(declaration.getOrg().getApis())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(this::initialiseApi)
                .collect(
                        Collectors.toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (listA, listB) -> {
                                    listA.addAll(listB);
                                    return listA;
                                })
                );
    }

    private Map.Entry<Api, List<DeclarativeGateway>> initialiseApi(DeclarativeApi modelApi) {
        Api api = new Api();
        api.setOrganizationId(orgId);
        api.setApiId(modelApi.getName());
        api.setVersion(modelApi.getVersion());

        DeclarativeApiConfig apiConfig = modelApi.getConfig();
        api.setEndpoint(apiConfig.getEndpoint());
        api.setPublicAPI(apiConfig.isMakePublic()); // Why is this different to publicApi?
        api.setEndpointType(apiConfig.getEndpointType());

        ofNullable(apiConfig.getSecurity()).ifPresent(declarativeEndpointProperties -> {
            Map<String, String> endpointProps = MappingUtil.map(declarativeEndpointProperties, EndpointProperties.class).toMap();
            api.setEndpointProperties(endpointProps);
        });
        // api.setParsePayload(); TODO this is not currently modelled
        // Use singular gatewayId or list of gateways
        List<DeclarativeGateway> gateways = ofNullable(apiConfig.getGateways())
                .orElse(Collections.singletonList(new ApiGateway(apiConfig.getGateway()))) // Hmm
                .stream()
                .map(ApiGateway::getGatewayId)
                .map(gatewaysMap::get)
                .collect(Collectors.toList());
        // Policy chain
        api.setApiPolicies(buildPolicyChain(modelApi.getPolicies()));
        return new AbstractMap.SimpleImmutableEntry<>(api, gateways);
    }

    private List<Policy> buildPolicyChain(List<DeclarativePolicy> policies) {
        return policies.stream()
                .map(declarativePolicy -> {
                    Policy policy = new Policy();
                    policy.setPolicyImpl(determinePolicyImpl(declarativePolicy));
                    policy.setPolicyJsonConfig(MappingUtil.safeWriteValueAsJson(declarativePolicy.getConfig()));
                    return policy;
                })
                .collect(Collectors.toList());
    }

    private String determinePolicyImpl(DeclarativePolicy declarativePolicy) {
        PolicyDefinitionBean policyDef;

        if (declarativePolicy.isPlugin()) {
            LOGGER.info("Resolving plugin: {}", declarativePolicy.getPlugin());
            // Get plugin reference first
            Plugin plugin = ofNullable(pluginMap.get(declarativePolicy.getPlugin()))
                    .orElseThrow(() -> new DeclarativeException("No such plugin exists: " + declarativePolicy.getPlugin()));
            try {
                policyDef = policyResolver.getPolicyDefinition(plugin.getCoordinates(), declarativePolicy.getName());
            } catch (InvalidPluginException e) {
                throw new DeclarativeException("Plugin could not be found: " + plugin.getCoordinates(), e);
            }
        } else {
            policyDef = policyResolver.getInbuiltPolicy(declarativePolicy.getName());
            LOGGER.debug("Loading inbuilt policy: {}", declarativePolicy.getName());
        }
        return policyDef.getPolicyImpl();
    }
}
