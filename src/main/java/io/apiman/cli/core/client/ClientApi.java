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
package io.apiman.cli.core.client;

import java.util.List;

import io.apiman.cli.core.client.model.ApiKey;
import io.apiman.cli.core.client.model.Client;
import io.apiman.cli.core.client.model.Contract;
import io.apiman.cli.core.client.model.DeclaredContract;
import io.apiman.manager.api.beans.clients.NewClientBean;
import io.apiman.manager.api.beans.clients.NewClientVersionBean;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
public interface ClientApi {
	@POST("/organizations/{orgName}/clients")
	Response create(@Path("orgName") String orgName, @Body NewClientBean client);

	@GET("/organizations/{orgName}/clients/{clientName}")
	Client fetch(@Path("orgName") String orgName, @Path("clientName") String clientName);

	@POST("/organizations/{orgName}/clients/{clientName}/versions")
	Client createVersion(@Path("orgName") String orgName, @Path("clientName") String clientName,
			@Body NewClientVersionBean client);

	@GET("/organizations/{orgName}/clients/{clientName}/versions/{version}")
	Client fetchVersion(@Path("orgName") String orgName, @Path("clientName") String clientName,
			@Path("version") String version);

	@GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/apikey")
	ApiKey getApiKey(@Path("orgName") String orgName, @Path("clientName") String clientName,
			@Path("version") String version);

	@POST("/organizations/{orgName}/clients/{clientName}/versions/{version}/contracts")
	Response createContract(@Path("orgName") String orgName, @Path("clientName") String clientName,
			@Path("version") String version, @Body Contract contract);

	@GET("/organizations/{orgName}/clients/{clientName}/versions/{version}/contracts")
	List<DeclaredContract> listContracts(@Path("orgName") String orgName, @Path("clientName") String clientName,
			@Path("version") String version);

}