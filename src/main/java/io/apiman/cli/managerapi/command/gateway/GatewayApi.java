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

package io.apiman.cli.managerapi.command.gateway;

import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.command.gateway.model.GatewayTestResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface GatewayApi {
    @POST("/gateways")
    Response create(@Body Gateway gateway);

    @GET("/gateways")
    List<Gateway> list();

    @GET("/gateways/{gatewayId}")
    Gateway fetch(@Path("gatewayId") String gatewayId);

    @PUT("/gateways")
    GatewayTestResponse test(@Body Gateway gateway);
}
