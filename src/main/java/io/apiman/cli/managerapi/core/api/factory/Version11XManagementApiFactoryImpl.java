/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.managerapi.core.api.factory;

import io.apiman.cli.managerapi.core.api.Version11xServerApi;
import io.apiman.cli.managerapi.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.*;
import io.apiman.cli.managerapi.management.factory.AbstractManagementApiFactory;
import io.apiman.cli.managerapi.management.factory.ManagementApiFactory;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import java.util.List;

import static io.apiman.cli.util.MappingUtil.MODEL_MAPPER;

/**
 * Provides legacy apiman 1.1.x support.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Version11XManagementApiFactoryImpl extends AbstractManagementApiFactory<VersionAgnosticApi, Version11xServerApi> implements ManagementApiFactory<VersionAgnosticApi> {
    @Override
    public VersionAgnosticApi build(String endpoint, String username, String password, boolean debugLogging) {
        final Version11xServerApi delegate = buildClient(Version11xServerApi.class, endpoint, username, password, debugLogging);

        return new VersionAgnosticApi() {
            @Override
            public Response create(String orgName, Api api) {
                return delegate.create(orgName, api);
            }

            @Override
            public Response createVersion(String orgName, String apiName, ApiVersion apiVersion) {
                return delegate.createVersion(orgName, apiName, apiVersion);
            }

            @Override
            public List<Api> list(String orgName) {
                return delegate.list(orgName);
            }

            @Override
            public Api fetch(String orgName, String apiName) {
                return delegate.fetch(orgName, apiName);
            }

            @Override
            public Api fetchVersion(String orgName, String apiName, String version) {
                return delegate.fetchVersion(orgName, apiName, version);
            }

            @Override
            public Response configure(String orgName, String apiName, String version, ApiConfig apiConfig) {
                // convert to 1.1.x format
                final ServiceConfig serviceConfig = MODEL_MAPPER.map(apiConfig, ServiceConfig.class);
                return delegate.configure(orgName, apiName, version, serviceConfig);
            }

            @Override
            public Response addPolicy(String orgName, String apiName, String version, ApiPolicy policyConfig) {
                return delegate.addPolicy(orgName, apiName, version, policyConfig);
            }

            @Override
            public Response setDefinition(String orgName, String apiName, String version, String definitionType, TypedString definition) {
                return delegate.setDefinition(orgName, apiName, version, definitionType, definition);
            }

            @Override
            public List<ApiPolicy> fetchPolicies(String orgName, String serviceName, String version) {
                return delegate.fetchPolicies(orgName, serviceName, version);
            }

            @Override
            public Response configurePolicy(String orgName, String apiName, String apiVersion, Long policyId, ApiPolicy policyConfig) {
                return delegate.configurePolicy(orgName, apiName, apiVersion, policyId, policyConfig);
            }
        };
    }

}
