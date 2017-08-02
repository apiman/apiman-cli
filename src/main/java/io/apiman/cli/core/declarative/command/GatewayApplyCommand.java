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

import static java.text.MessageFormat.format;
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
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.gateway.model.GatewayConfig;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.DeclarativeException;
import io.apiman.cli.management.factory.GatewayApiFactory;
import io.apiman.cli.util.MappingUtil;
import io.apiman.cli.util.PolicyResolver;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Policy;
import io.apiman.gateway.engine.beans.SystemStatus;
import io.apiman.manager.api.beans.policies.PolicyDefinitionBean;
import io.apiman.manager.api.core.exceptions.InvalidPluginException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.RetrofitError;

/**
 * Apply a gateway declaration.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayApplyCommand extends AbstractApplyCommand {

    private static final Logger LOGGER = LogManager.getLogger(GatewayApplyCommand.class);
    private String orgId;
    private Map<String, DeclarativeGateway> gatewaysMap;
    private Map<String, Plugin> pluginMap;
    private Map<Api, List<DeclarativeGateway>> apisToPublish;
    private GatewayApiFactory apiFactory;
    private PolicyResolver policyResolver;

    @Inject
    public void setGatewayApiFactory(GatewayApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Inject
    public void setPolicyResolver(PolicyResolver policyResolver) {
        this.policyResolver = policyResolver;
    }

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
                .filter(this::isActive)
                .collect(Collectors.toMap(Gateway::getName, gw -> gw));

        LOGGER.debug("Gateways map: {}", gatewaysMap);

        pluginMap = buildPluginMap(declaration);
        LOGGER.debug("Plugin map: {}", pluginMap);

        apisToPublish = buildApis(declaration);
        LOGGER.debug("APIs to publication map: {}", apisToPublish);

        publishAll();
    }

    private boolean isActive(DeclarativeGateway gateway) {
        LOGGER.debug("Checking Gateway {} status", gateway.getName());
        GatewayApi client = buildGatewayApiClient(gateway.getConfig(), getLogDebug());
        return statusCheck(client, gateway);
    }

    private Map<String, Plugin> buildPluginMap(BaseDeclaration declaration) {
        Map<String, Plugin> pluginMap = new LinkedHashMap<>();
        List<Plugin> pluginsList = ofNullable(declaration.getSystem().getPlugins())
                .orElse(Collections.emptyList());

        pluginsList.stream()
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

    private void publishAll() {
        apisToPublish.entrySet().forEach(entry -> {
            List<DeclarativeGateway> publishTo = entry.getValue();
            Api api = entry.getKey();
            publishTo.forEach(gateway -> publishApi(api, gateway));
        });
    }

    private void publishApi(Api api, DeclarativeGateway gateway) {
        GatewayConfig config = gateway.getConfig();
        GatewayApi client = buildGatewayApiClient(config, getLogDebug());
        LOGGER.info("Publishing {} to {}", api, gateway.getConfig().getEndpoint());
        callAndCatch(gateway, () -> client.publishApi(api));
    }

    private GatewayApi buildGatewayApiClient(GatewayConfig config, boolean debugLogging) {
        return apiFactory.build(
                config.getEndpoint(),
                config.getUsername(),
                config.getPassword(),
                debugLogging);
    }

    private boolean statusCheck(GatewayApi client, DeclarativeGateway gateway) {
        SystemStatus status = callAndCatch(gateway, () -> client.getSystemStatus());
        LOGGER.debug("Gateway status: {}", status);
        if (!status.isUp()) {
            throw new StatusCheckException(gateway, "Status indicates gateway is currently down");
        }
        return status.isUp();
    }

    private <T> T callAndCatch(DeclarativeGateway gateway, Supplier<T> action) {
        try {
            return action.get();
        } catch(RetrofitError e) {
            LOGGER.debug("Endpoint: {}, RetrofitError: {}", gateway.getConfig().getEndpoint(), e);
            switch (e.getKind()) {
                case NETWORK:
                    throw new StatusCheckException(gateway, "Network issue: " + e.getMessage());
                case CONVERSION:
                    throw e;
                case HTTP:
                    throw new StatusCheckException(gateway,
                            format("Unsuccessful response code: {0} {1}",
                                    e.getResponse().getStatus(),
                                    e.getResponse().getReason()));
                case UNEXPECTED:
                    throw new StatusCheckException(gateway, format("Unexpected exception: {0}", e.getMessage()));
            }
            throw e;
        }
    }

    private class StatusCheckException extends CommandException {

        public StatusCheckException(DeclarativeGateway gateway, String message) {
            super(format("Status check failed on Gateway {0} ({1}). {2}",
                    gateway.getName(),
                    gateway.getConfig().getEndpoint(),
                    message));
        }
    }
}
