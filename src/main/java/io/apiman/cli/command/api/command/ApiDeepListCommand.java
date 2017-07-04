/*
 * Copyright 2017 jean-Charles Quantin
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

import io.apiman.cli.command.api.ApiMixin;
import io.apiman.cli.command.api.VersionAgnosticApi;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.declarative.model.DeclarativeApi;
import io.apiman.cli.command.declarative.model.DeclarativePolicy;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Deep List of APIs : with versions and policies
 *
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
public class ApiDeepListCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiDeepListCommand.class);

    @Inject
    public ApiDeepListCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        LOGGER.debug("Listing {}", this::getModelName);

        VersionAgnosticApi apiClient = buildServerApiClient(VersionAgnosticApi.class, serverVersion);
        final List<Api> apis = apiClient.list(orgName);
        final List<DeclarativeApi> deepApisList = new ArrayList<DeclarativeApi>();
        apis.forEach(api -> {
            apiClient.fetchVersions(orgName, api.getName()).forEach(apiVersion -> {
                final DeclarativeApi declApi = MappingUtil.map(apiVersion, DeclarativeApi.class);
                final List<DeclarativePolicy> ListPolicies = new ArrayList<DeclarativePolicy>();
                apiClient.fetchPolicies(orgName, api.getName(), apiVersion.getVersion())
                    .forEach(policy -> {
                            ApiPolicy apiPolicy = apiClient.fetchPolicy(orgName, api.getName(), apiVersion.getVersion(), policy.getId());
                            DeclarativePolicy declarativePolicy = MappingUtil.map(apiPolicy, DeclarativePolicy.class);
                            declarativePolicy.setName(policy.getPolicyDefinitionId());
                            declarativePolicy.setId(policy.getId().toString());
                            declarativePolicy.setConfig(MappingUtil.safeGetValueFromJson(apiPolicy.getConfiguration()));
                            ListPolicies.add(declarativePolicy);
                        });
                declApi.setPolicies(ListPolicies);
                deepApisList.add(declApi);
            });
        });
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(deepApisList));

    }
}
