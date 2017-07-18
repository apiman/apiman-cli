/*
 * Copyright 2017 Red Hat, Inc.
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

import java.util.List;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public interface GatewayApi {

    @GET("/system/status")
    SystemStatus getSystemStatus();

    @PUT("/apis")
    Response publishApi(@Body Api api);

    @DELETE("/apis/{organizationId}/{apiId}/{version}")
    Response retireApi(@Path("organizationId") String organizationId,
                       @Path("apiId") String apiId,
                       @Path("version") String version);

    @GET("/apis/{organizationId}/{apiId}/{version}/endpoint")
    ApiEndpoint getApiEndpoint(@Path("organizationId") String organizationId,
                               @Path("apiId") String apiId,
                               @Path("version") String version);

    @PUT("/clients")
    Response registerClient(@Body Client client);

    @DELETE("/clients/{organizationId}/{clientId}/{version}")
    Response unregisterClient(@Path("organizationId") String organizationId,
                              @Path("clientId") String clientId,
                              @Path("version") String version);

    /**
     * New API with more REST-friendly structure.
     **/
    @GET("/organizations/{organizationId}/apis/{apiId}/versions/{version}/endpoint")
    ApiEndpoint getApiEndpoint2(@Path("organizationId") String organizationId,
                                @Path("apiId") String apiId,
                                @Path("version") String version);

    // API
    @DELETE("/organizations/{organizationId}/apis/{apiId}/versions/{version}")
    Response retireApi2(@Path("organizationId") String organizationId,
                        @Path("apiId") String apiId,
                        @Path("version") String version);

    @GET("/organizations/{organizationId}/apis/")
    List<String> listApis(@Path("organizationId") String organizationId);

    @GET("/organizations/{organizationId}/apis/{apiId}/versions")
    List<String> listApiVersions(@Path("organizationId") String organizationId,
                                 @Path("apiId") String apiId);

    @GET("/organizations/{organizationId}/apis/{apiId}/versions/{version}")
    Api getApiVersion(@Path("organizationId") String organizationId,
                      @Path("apiId") String apiId,
                      @Path("version") String version);

    // Client
    @DELETE("/organizations/{organizationId}/clients/{clientId}/versions/{version}")
    Response unregister(@Path("organizationId") String organizationId,
                        @Path("clientId") String clientId,
                        @Path("version") String version);

    @GET("/organizations/{organizationId}/clients/")
    List<String> listClients(@Path("organizationId") String organizationId);

    @GET("/organizations/{organizationId}/clients/{clientId}/versions")
    List<String> listClientVersions(@Path("organizationId") String organizationId,
                                    @Path("clientId") String clientId);

    @GET("/organizations/{organizationId}/clients/{clientId}/versions/{version}")
    Client getClientVersion(@Path("organizationId") String organizationId,
                            @Path("clientId") String clientId,
                            @Path("version") String version);

    // Org
    @GET("/organizations")
    List<String> listOrgs();

}
