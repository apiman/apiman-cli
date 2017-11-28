/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.core.api;

import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.ApiEndpoint;
import io.apiman.gateway.engine.beans.Client;
import io.apiman.gateway.engine.beans.SystemStatus;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface GatewayApi {

    @PUT("/apis")
    Response publishApi(@Body Api api);

    @DELETE("/apis/{organizationId}/{apiId}/{version}")
    Response retireApi(@Path("organizationId") String organizationId,
            @Path("apiId") String apiId, @Path("version") String version);

    @GET("/apis/{organizationId}/{apiId}/{version}/endpoint")
    ApiEndpoint getApiEndpoint(@Path("organizationId") String organizationId,
            @Path("apiId") String apiId, @Path("version") String version);

    @GET("/system/status")
    SystemStatus getSystemStatus();

    @PUT("/clients")
    void registerClient(@Body Client client);

    @DELETE("/clients/{organizationId}/{clientId}/{version}")
    void unregisterClient(@Path("organizationId") String organizationId,
            @Path("clientId") String clientId, @Path("version") String version);

}
