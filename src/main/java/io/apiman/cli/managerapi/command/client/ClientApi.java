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
package io.apiman.cli.managerapi.command.client;


import io.apiman.cli.command.api.model.EntityVersion;
import io.apiman.cli.command.client.model.ApiKey;
import io.apiman.cli.command.client.model.Client;
import io.apiman.cli.command.client.model.Contract;
import io.apiman.cli.managerapi.command.api.PolicyApi;
import io.apiman.manager.api.beans.clients.ClientVersionBean;
import retrofit.client.Response;
import retrofit.http.Path;

import java.util.List;

/**
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
public interface ClientApi extends PolicyApi {

    List<Client> list(String orgName); // Hack to display only name, seemingly.

	List<Client> listVersions(@Path("orgName") String orgName, @Path("clientName") String clientName); // Hack to display only version, seemingly.

	Response create(String orgName, Client client);

	Client fetch(String orgName, String clientName);

	Client createVersion(String orgName, String clientName, EntityVersion client);

	ClientVersionBean fetchVersion(String orgName, String clientName, String version);

	ApiKey getApiKey(String orgName, String clientName, String version);

	Response createContract(String orgName, String clientName, String version, Contract contract);
	
	List<Contract> listContracts(String orgName, String clientName, String version);



}
