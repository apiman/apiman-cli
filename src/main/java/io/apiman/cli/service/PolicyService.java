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

package io.apiman.cli.service;

import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;

import java.util.List;

/**
 * Manages policies.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface PolicyService {
    /**
     * Fetch the policies attached to the specified API.
     *
     * @param serverVersion the management server API version
     * @param orgName       the organisation name
     * @param apiName       the API name
     * @param apiVersion    the API version
     * @return the policies
     */
    List<ApiPolicy> fetchPolicies(ManagementApiVersion serverVersion, String orgName,
                                  String apiName, String apiVersion);

    /**
     * Apply the policies to the specified API.
     *
     * @param serverVersion the management server API version
     * @param orgName       the organisation name
     * @param apiName       the API name
     * @param apiVersion    the API version
     * @param apiPolicies   the policies to apply.
     * @param policyName    the policy name
     * @param apiPolicy     the policy to apply
     */
    void applyPolicies(ManagementApiVersion serverVersion, String orgName,
                       String apiName, String apiVersion, List<ApiPolicy> apiPolicies,
                       String policyName, ApiPolicy apiPolicy);
}
