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

import com.google.inject.Inject;
import io.apiman.cli.core.api.GatewayApi;
import io.apiman.cli.core.api.model.ApiGateway;
import io.apiman.cli.core.api.model.EndpointProperties;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.core.declarative.model.DeclarativeApi;
import io.apiman.cli.core.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.core.declarative.model.DeclarativeGateway;
import io.apiman.cli.core.declarative.model.DeclarativePolicy;
import io.apiman.cli.core.gateway.model.GatewayConfig;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.management.factory.GatewayApiFactory;
import io.apiman.cli.util.MappingUtil;
import io.apiman.cli.util.PluginRegistry;
import io.apiman.cli.util.PluginRegistry.PluginResolver;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Policy;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Apply a gateway declaration.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayApplyCommand extends AbstractApplyCommand {

    private static final Logger LOGGER = LogManager.getLogger(GatewayApplyCommand.class);

    private String orgId;
    private PluginResolver policyResolver = PluginRegistry.getResolver();
    private Map<String, DeclarativeGateway> gatewaysMap;
    private Map<String, Plugin> pluginMap;
    private Map<Api, List<DeclarativeGateway>> apisToPublish;
    private GatewayApiFactory apiFactory;

    @Override
    protected String getCommandDescription() {
        return "Apply gateway declaration";
    }

    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        orgId = declaration.getOrg().getName();
        LOGGER.debug("Organization ID: {}", orgId);

        gatewaysMap = declaration.getSystem().getGateways()
                .stream()
                .collect(Collectors.toMap(gw -> gw.getName(), gw -> gw));
        LOGGER.debug("Gateways map: {}", gatewaysMap);

        pluginMap = buildPluginMap(declaration);
        LOGGER.debug("Plugin map: {}", pluginMap);

        apisToPublish = buildApis(declaration);
        LOGGER.debug("APIs to publication map: {}", apisToPublish);

        publishAll();
    }

    private Map<String, Plugin> buildPluginMap(BaseDeclaration declaration) {
        Map<String, Plugin> pluginMap = new LinkedHashMap<>();
        List<Plugin> pluginsList = ofNullable(declaration.getSystem().getPlugins())
                .orElse(Collections.emptyList());

        pluginsList.stream()
                .forEach(plugin -> {
                    if (plugin.getName() == null) {
                        LOGGER.info("A plugin has been specified without a friendly name. References must be by its full coordinates: {}", plugin.getCoordinates());
                    } else {
                        pluginMap.put(plugin.getName(), plugin);
                    }
                    pluginMap.put(plugin.getCoordinates().toString(), plugin);
                });
        return pluginMap;
    }

    // Build map of APIs to Gateway that they should be published on.
    private Map<Api, List<DeclarativeGateway>> buildApis(BaseDeclaration declaration) {
        return ofNullable(declaration.getOrg().getApis())
                .map(Collection::stream)
                .orElseGet(() -> Stream.<DeclarativeApi>empty())
                .map(this::initialiseApi)
                .collect(
                        Collectors.toMap(api -> api.getKey(),
                                api -> api.getValue(),
                                (listA, listB) -> {
                                    listA.addAll(listB);
                                    return listA;
                                })
                        );
    }

    private Entry<Api, List<DeclarativeGateway>> initialiseApi(DeclarativeApi modelApi) {
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
                .orElse(Arrays.asList(new ApiGateway(apiConfig.getGateway()))) // Hmm
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
        PolicyDefinitionBean policyDef = null;

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
            policyDef = ofNullable(policyResolver.getInbuiltPolicy(declarativePolicy.getName()))
                    .orElseThrow(() -> new DeclarativeException("No such policy exists: " + declarativePolicy.getName()));
            LOGGER.debug("Loading inbuilt policy: {}", declarativePolicy.getName());
        }
        return policyDef.getPolicyImpl();
    }

    private void publishAll() {
        apisToPublish.entrySet().forEach(entry -> {
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

        LOGGER.info("Publishing {} to {}", api, gateway.getConfig().getEndpoint());
        client.publishApi(api);
    }

    private GatewayApi buildGatewayApiClient(String endpoint, String username, String password, boolean debugLogging) {
        return apiFactory.build(
                endpoint,
                username,
                password,
                debugLogging);
    }

    @Inject
    public void setGatewayApiFactory(GatewayApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

}
