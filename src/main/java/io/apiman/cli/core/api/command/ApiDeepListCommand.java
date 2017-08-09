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

package io.apiman.cli.core.api.command;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.apiman.cli.core.api.ApiMixin;
import io.apiman.cli.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.core.declarative.model.BaseDeclaration;
import io.apiman.cli.core.declarative.model.DeclarativeApi;
import io.apiman.cli.core.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.core.declarative.model.DeclarativeOrg;
import io.apiman.cli.core.declarative.model.DeclarativePlan;
import io.apiman.cli.core.declarative.model.DeclarativePolicy;
import io.apiman.cli.core.plan.PlanApi;
import io.apiman.cli.core.plan.model.Plan;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;

/**
 * Deep List of APIs : with versions and policies
 *
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
public class ApiDeepListCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiDeepListCommand.class);

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        final BaseDeclaration baseDeclaration = getDeepListBaseDeclaration();
        LOGGER.debug("Outputting the BaseDeclaration");
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(baseDeclaration));
    }

    public BaseDeclaration getDeepListBaseDeclaration() {
        LOGGER.debug("Listing {}", this::getModelName);

        final BaseDeclaration baseDeclaration = new BaseDeclaration();
        LOGGER.debug("Creating a BaseDeclaration root object");
        try {
            baseDeclaration
                    .setOrg(MappingUtil.JSON_MAPPER.readValue("{\"name\": \"" + orgName + "\"}", DeclarativeOrg.class));
        } catch (IOException e) {
            LogUtil.OUTPUT.error("deserialisation error");
            throw new CommandException(e);
        }

        LOGGER.debug("Populating the BaseDeclaration with apis");
        baseDeclaration.getOrg().setApis(new ArrayList<DeclarativeApi>());

        VersionAgnosticApi apiClient = buildServerApiClient(VersionAgnosticApi.class, serverVersion);
        final List<Api> apis = apiClient.list(orgName);

        apis.forEach(api -> {
            apiClient.fetchVersions(orgName, api.getName()).forEach(apiVersion -> {
                
                apiVersion.clearStatus();
                final DeclarativeApi declApi = MappingUtil.map(apiVersion, DeclarativeApi.class);
                declApi.setOrganizationName(null);
                declApi.setConfig(
                        MappingUtil.map(apiClient.fetchVersionConfig(orgName, api.getName(), apiVersion.getVersion()),
                                DeclarativeApiConfig.class));
                declApi.getConfig().setGateway("");
                declApi.getConfig().getGateways().forEach(gw -> {
                    declApi.getConfig().setGateway(declApi.getConfig().getGateway() + " " + gw.getGatewayId());
                });

                final List<DeclarativePolicy> listPolicies = new ArrayList<DeclarativePolicy>();
                apiClient.fetchPolicies(orgName, api.getName(), apiVersion.getVersion()).forEach(policy -> {
                    ApiPolicy apiPolicy = apiClient.fetchPolicy(orgName, api.getName(), apiVersion.getVersion(),
                            policy.getId());
                    DeclarativePolicy declarativePolicy = MappingUtil.map(apiPolicy, DeclarativePolicy.class);
                    declarativePolicy.setName(policy.getPolicyDefinitionId());
                    declarativePolicy.setId(policy.getId().toString());
                    try {
                        declarativePolicy
                                .setConfig(MappingUtil.JSON_MAPPER.writeValueAsString(apiPolicy.getConfiguration()));
                    } catch (JsonProcessingException e) {
                        LogUtil.OUTPUT.error("APIPolicy serialisation error");
                        throw new CommandException(e);
                    }

                    listPolicies.add(declarativePolicy);
                });
                declApi.setPolicies(listPolicies);
                baseDeclaration.getOrg().getApis().add(declApi);
            });
        });
        LOGGER.debug("Populating the BaseDeclaration with plans");
        baseDeclaration.getOrg().setPlans(new ArrayList<DeclarativePlan>());
        PlanApi planClient = buildServerApiClient(PlanApi.class);
        final List<Plan> plans = planClient.list(orgName);

        plans.forEach(plan -> {
            planClient.fetchVersions(orgName, plan.getName()).forEach(planVersion -> {
                
                final DeclarativePlan declPlan = MappingUtil.map(planVersion, DeclarativePlan.class);

                final List<DeclarativePolicy> listPolicies = new ArrayList<DeclarativePolicy>();
                planClient.fetchPolicies(orgName, plan.getName(), planVersion.getVersion()).forEach(policy -> {
                    ApiPolicy apiPolicy = planClient.fetchPolicy(orgName, plan.getName(), planVersion.getVersion(),
                            policy.getId());
                    DeclarativePolicy declarativePolicy = MappingUtil.map(apiPolicy, DeclarativePolicy.class);
                    declarativePolicy.setName(policy.getPolicyDefinitionId());
                    declarativePolicy.setId(policy.getId().toString());
                    try {
                        declarativePolicy
                                .setConfig(MappingUtil.JSON_MAPPER.writeValueAsString(apiPolicy.getConfiguration()));
                    } catch (JsonProcessingException e) {
                        LogUtil.OUTPUT.error("APIPolicy serialisation error");
                        throw new CommandException(e);
                    }

                    listPolicies.add(declarativePolicy);
                });
                declPlan.setPolicies(listPolicies);
                baseDeclaration.getOrg().getPlans().add(declPlan);
            });
        });
        
        
        
        
        
        return baseDeclaration;
    }
}