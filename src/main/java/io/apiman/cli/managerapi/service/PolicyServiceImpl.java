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

package io.apiman.cli.managerapi.service;

import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.managerapi.command.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static io.apiman.cli.managerapi.command.common.model.ManagementApiVersion.v12x;

/**
 * Manages policies.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class PolicyServiceImpl implements PolicyService {
    private static final Logger LOGGER = LogManager.getLogger(PolicyServiceImpl.class);

    private ManagementApiService managementApiService;

    @Inject
    public PolicyServiceImpl(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ApiPolicy> fetchPolicies(ManagementApiVersion serverVersion, String orgName,
                                         String apiName, String apiVersion) {

        final VersionAgnosticApi apiClient = managementApiService.buildServerApiClient(VersionAgnosticApi.class, serverVersion);
        return apiClient.fetchPolicies(orgName, apiName, apiVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyPolicies(ManagementApiVersion serverVersion, String orgName,
                              String apiName, String apiVersion, List<ApiPolicy> apiPolicies,
                              String policyName, ApiPolicy apiPolicy) {

        final VersionAgnosticApi apiClient = managementApiService.buildServerApiClient(VersionAgnosticApi.class, serverVersion);

        // determine if the policy already exists for this API
        final Optional<ApiPolicy> existingPolicy = apiPolicies.stream()
                .filter(p -> policyName.equals(p.getPolicyDefinitionId()))
                .findFirst();

        if (existingPolicy.isPresent()) {
            if (v12x.equals(serverVersion)) {
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
    }
}
