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

package io.apiman.cli.managerapi.command.api.factory;

import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.EntityVersion;
import io.apiman.cli.managerapi.command.api.Version12xServerApi;
import io.apiman.cli.managerapi.command.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.management.factory.AbstractManagementApiFactory;
import io.apiman.cli.managerapi.management.factory.ManagementApiFactory;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import java.util.List;

/**
 * Provides apiman 1.2.x support.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Version12XManagementApiFactoryImpl extends AbstractManagementApiFactory<VersionAgnosticApi, Version12xServerApi> implements ManagementApiFactory<VersionAgnosticApi> {
    @Override
    public VersionAgnosticApi build(String endpoint, String username, String password, boolean debugLogging) {
        final Version12xServerApi delegate = buildClient(Version12xServerApi.class, endpoint, username, password, debugLogging);

        return new VersionAgnosticApi() {
            @Override
            public Response create(String orgName, Api api) {
                return delegate.create(orgName, api);
            }

            @Override
            public Response createVersion(String orgName, String apiName, EntityVersion apiVersion) {
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
            	Api api = delegate.fetchVersion(orgName, apiName, version);
            	api.setOrganizationName(orgName);
            	api.setName(apiName);
                return api;
            }

            @Override
            public List<Api> fetchVersions(String orgName, String apiName) {
                return delegate.fetchVersions(orgName, apiName);
            }
            
            @Override
            public ApiConfig fetchVersionConfig(String orgName, String apiName, String version) {
                return delegate.fetchVersionConfig(orgName, apiName, version);
            }

            @Override
            public Response configure(String orgName, String apiName, String version, ApiConfig apiConfig) {
                return delegate.configure(orgName, apiName, version, apiConfig);
            }

            @Override
            public Response addPolicy(String orgName, String apiName, String version, ApiPolicy policyConfig) {
                return delegate.addPolicy(orgName, apiName, version, policyConfig);
            }

            @Override
            public Response setDefinition(String orgName, String apiName, String version, String definitionType, TypedString definition) {
                return  delegate.setDefinition(orgName, apiName, version, definitionType, definition);
            }

            @Override
            public List<ApiPolicy> fetchPolicies(String orgName, String serviceName, String version) {
                return delegate.fetchPolicies(orgName, serviceName, version);
            }

            @Override
            public ApiPolicy fetchPolicy(String orgName, String serviceName, String version, Long policyId) {
                return delegate.fetchPolicy(orgName, serviceName, version, policyId);
            }

            @Override
            public Response configurePolicy(String orgName, String apiName, String apiVersion, Long policyId, ApiPolicy policyConfig) {
                return delegate.configurePolicy(orgName, apiName, apiVersion, policyId, policyConfig);
            }
        };
    }
}
