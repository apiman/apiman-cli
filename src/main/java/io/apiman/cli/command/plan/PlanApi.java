/*
 * Copyright 2017 Jean-Charles Quantin
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

package io.apiman.cli.command.plan;

import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.command.plan.model.Plan;
import io.apiman.cli.command.plan.model.PlanVersion;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

/**
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
public interface PlanApi {
    @POST("/organizations/{orgName}/plans")
    Response create(@Path("orgName") String orgName, @Body Plan plan);

    @POST("/organizations/{orgName}/plans/{planName}/versions")
    Response createVersion(@Path("orgName") String orgName, @Path("planName") String planName, @Body PlanVersion apiVersion);

    @GET("/organizations/{orgName}/plans")
    List<Plan> list(@Path("orgName") String orgName);

    @GET("/organizations/{orgName}/plans/{planName}")
    Plan fetch(@Path("orgName") String orgName, @Path("planName") String planName);

    @GET("/organizations/{orgName}/plans/{planName}/versions/{version}")
    Plan fetchVersion(@Path("orgName") String orgName, @Path("planName") String planName, @Path("version") String version);

    @POST("/organizations/{orgName}/plans/{planName}/versions/{version}/policies")
    Response addPolicy(@Path("orgName") String orgName, @Path("planName") String apiName,
                       @Path("version") String version, @Body ApiPolicy policyConfig);

    @GET("/organizations/{orgName}/plans/{apiName}/versions/{version}/policies")
    List<ApiPolicy> fetchPolicies(@Path("orgName") String orgName, @Path("apiName") String apiName,
                                  @Path("version") String version);

}
