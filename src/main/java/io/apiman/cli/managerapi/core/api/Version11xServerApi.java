/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.managerapi.core.api;

import io.apiman.cli.core.api.model.*;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedString;

import java.util.List;

/**
 * Legacy support for apiman 1.1.x.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface Version11xServerApi {
    @POST("/organizations/{orgName}/services")
    Response create(@Path("orgName") String orgName, @Body Api api);

    @POST("/organizations/{orgName}/services/{serviceName}/versions")
    Response createVersion(@Path("orgName") String orgName, @Path("serviceName") String serviceName, @Body ApiVersion apiVersion);

    @GET("/organizations/{orgName}/services")
    List<Api> list(@Path("orgName") String orgName);

    @GET("/organizations/{orgName}/services/{serviceName}")
    Api fetch(@Path("orgName") String orgName, @Path("serviceName") String serviceName);

    @GET("/organizations/{orgName}/services/{serviceName}/versions/{version}")
    Api fetchVersion(@Path("orgName") String orgName, @Path("serviceName") String serviceName, @Path("version") String version);

    @PUT("/organizations/{orgName}/services/{serviceName}/versions/{version}")
    Response configure(@Path("orgName") String orgName, @Path("serviceName") String serviceName,
                       @Path("version") String version, @Body ServiceConfig config);

    @PUT("/organizations/{orgName}/services/{serviceName}/versions/{version}/definition")
    Response setDefinition(@Path("orgName") String orgName, @Path("serviceName") String serviceName,
                           @Path("version") String version, @Header("Content-Type") String type, @Body TypedString content);

    @POST("/organizations/{orgName}/services/{serviceName}/versions/{version}/policies")
    Response addPolicy(@Path("orgName") String orgName, @Path("serviceName") String serviceName,
                       @Path("version") String version, @Body ApiPolicy policyConfig);

    @GET("/organizations/{orgName}/services/{serviceName}/versions/{version}/policies")
    List<ApiPolicy> fetchPolicies(@Path("orgName") String orgName, @Path("serviceName") String serviceName,
                                  @Path("version") String version);

    @PUT("/organizations/{orgName}/services/{serviceName}/versions/{version}/policies/{policyId}")
    Response configurePolicy(@Path("orgName") String orgName, @Path("serviceName") String serviceName,
                             @Path("version") String version, @Path("policyId") Long policyId, @Body ApiPolicy policyConfig);
}
