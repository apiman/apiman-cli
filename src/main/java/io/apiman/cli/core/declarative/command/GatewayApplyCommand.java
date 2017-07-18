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
import io.apiman.cli.core.gateway.model.GatewayConfig;
import io.apiman.cli.core.plugin.model.Plugin;
import io.apiman.cli.management.ManagementApiUtil;
import io.apiman.cli.util.DeclarativeUtil;
import io.apiman.cli.util.MappingUtil;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.Client;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    private Map<Api, List<DeclarativeGateway>> publishToGateway = new LinkedHashMap<>();

    private Map<String, Client> clientMap = new LinkedHashMap<>(); // TODO

    private String orgId;

    private Map<String, DeclarativeGateway> gateways = new LinkedHashMap<>();
    private List<Plugin> policies;


//    private Map<String, Plugin> pluginsByName = new LinkedHashMap<>();
//    private Map<Long, Plugin> pluginsById = new LinkedHashMap<>();

    @Override
    protected String getCommandDescription() {
        return "Apply gateway declaration";
    }

    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        orgId = declaration.getOrg().getName();
        gateways = declaration.getSystem().getGateways().stream().collect(Collectors.toMap(gw -> gw.getName(), gw -> gw));
        policies = ofNullable(declaration.getSystem().getPlugins()).orElse(Collections.emptyList());

        initialiseApis(declaration);

        applyPlugins(declaration);

        buildClients(declaration);

        publish();
    }

    private void publish() {
        publishToGateway.entrySet().forEach(entry -> {
            List<DeclarativeGateway> publishTo = entry.getValue();
            Api api = entry.getKey();

            publishTo.forEach(gateway -> publishApi(api, gateway));
        });
    }

    private void publishApi(Api api, DeclarativeGateway gateway) {
        LOGGER.info("Publishing {} to {}", api, gateway.getConfig().getEndpoint());
        GatewayConfig config = gateway.getConfig();
        buildGatewayApiClient(config.getEndpoint(),
                config.getUsername(),
                config.getPassword(),
                getLogDebug());
    }

    private void initialiseApis(BaseDeclaration declaration) {
        ofNullable(declaration.getOrg().getApis()).ifPresent(apis -> {
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

        System.out.println("Prepared API... "  + api);

        return api;
    }

    // TODO bad name, just builds the map
    private void publishApiOnGateway(Api api, List<String> gatewayIds) {
        for (String id : gatewayIds) {
            // Look up gateway by ID
            DeclarativeGateway gw = gateways.get(id);
            // For given API publish to indicated GW
            publishToGateway.get(api).add(gw);
        }
    }

    private String getKey(DeclarativeApi modelApi) {
        return modelApi.getName() + "-" + modelApi.getVersion(); //$NON-NLS-1$
    }

    private void applyPlugins(BaseDeclaration declaration) {
        //declaration.get
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
}
