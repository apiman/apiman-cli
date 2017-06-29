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

package io.apiman.cli.command.api.command;

import com.google.common.collect.Lists;
import io.apiman.cli.command.api.ApiMixin;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiGateway;
import io.apiman.cli.command.api.VersionAgnosticApi;
import io.apiman.cli.command.api.model.ApiPlan;
import io.apiman.cli.command.plan.PlanApi;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.management.ManagementApiUtil;
import io.apiman.cli.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Create an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiCreateCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiCreateCommand.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "API name", required = true)
    private String name;

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Option(name = "--initialVersion", aliases = {"-v"}, usage = "Initial version", required = true)
    private String initialVersion;

    @Option(name = "--endpoint", aliases = {"-e"}, usage = "Endpoint", required = true)
    private String endpoint;

    @Option(name = "--endpointType", aliases = {"-t"}, usage = "Endpoint type")
    private String endpointType = "rest";

    @Option(name = "--public", aliases = {"-p"}, usage = "Public API", required = true)
    private boolean publicApi;

    @Option(name = "--gateway", aliases = {"-g"}, usage = "Gateway")
    private List<String> gateway = new ArrayList<String>();

    @Inject
    public ApiCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        LOGGER.debug("Creating {}", this::getModelName);

        final Api api = new Api(
                name,
                description,
                initialVersion);

        if (gateway.size()==0) { gateway.add("TheGateway"); }
        // populating the list of Gateway
        final List<ApiGateway> gatewaysList = Lists.newArrayList();
        gateway.forEach(strGat -> { gatewaysList.add(new ApiGateway(strGat)); });
        
        final ApiConfig config = new ApiConfig(
                endpoint,
                endpointType,
                publicApi,
                gatewaysList,
                new ArrayList<ApiPlan>());

        // create
        final VersionAgnosticApi apiClient = buildServerApiClient(VersionAgnosticApi.class, serverVersion);
        ManagementApiUtil.invokeAndCheckResponse(() -> apiClient.create(orgName, api));

        // configure
        ManagementApiUtil.invokeAndCheckResponse(() -> apiClient.configure(orgName, name, initialVersion, config));
    }
}
