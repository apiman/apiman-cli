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

package io.apiman.cli.command.api;

import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.api.model.ApiVersion;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * Support for apiman 1.2.x.
 * 
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface Version12xServerApi {
    @POST("/organizations/{orgName}/apis")
    Response create(@Path("orgName") String orgName, @Body Api api);

    @POST("/organizations/{orgName}/apis/{apiName}/versions")
    Response createVersion(@Path("orgName") String orgName, @Path("apiName") String apiName, @Body ApiVersion apiVersion);

    @GET("/organizations/{orgName}/apis")
    List<Api> list(@Path("orgName") String orgName);

    @GET("/organizations/{orgName}/apis/{apiName}")
    Api fetch(@Path("orgName") String orgName, @Path("apiName") String apiName);

    @GET("/organizations/{orgName}/apis/{apiName}/versions/{version}")
    Api fetchVersion(@Path("orgName") String orgName, @Path("apiName") String apiName, @Path("version") String version);

    @GET("/organizations/{orgName}/apis/{apiName}/versions")
    List<Api> fetchVersions(@Path("orgName") String orgName, @Path("apiName") String apiName);

    @PUT("/organizations/{orgName}/apis/{apiName}/versions/{version}")
    Response configure(@Path("orgName") String orgName, @Path("apiName") String apiName,
                       @Path("version") String version, @Body ApiConfig config);

    @POST("/organizations/{orgName}/apis/{apiName}/versions/{version}/policies")
    Response addPolicy(@Path("orgName") String orgName, @Path("apiName") String apiName,
                       @Path("version") String version, @Body ApiPolicy policyConfig);

    @GET("/organizations/{orgName}/apis/{apiName}/versions/{version}/policies")
    List<ApiPolicy> fetchPolicies(@Path("orgName") String orgName, @Path("apiName") String apiName,
                                  @Path("version") String version);

    @PUT("/organizations/{orgName}/apis/{apiName}/versions/{version}/policies/{policyId}")
    Response configurePolicy(@Path("orgName") String orgName, @Path("apiName") String apiName,
                             @Path("version") String version, @Path("policyId") Long policyId, @Body ApiPolicy policyConfig);
}
