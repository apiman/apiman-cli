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

package io.apiman.cli.managerapi;

import com.beust.jcommander.Parameter;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.service.ManagementApiService;

import static io.apiman.cli.util.AuthUtil.DEFAULT_SERVER_PASSWORD;
import static io.apiman.cli.util.AuthUtil.DEFAULT_SERVER_USERNAME;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagerCommon {
    private final String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman";
    private final ManagementApiService managementApiService;

    @Parameter(names = { "--server", "-s" }, description = "Management API server address")
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

    @Parameter(names = { "--serverUsername", "-su"}, description = "Management API server username")
    private String serverUsername = DEFAULT_SERVER_USERNAME;

    @Parameter(names = { "--serverPassword", "-sp"}, description = "Management API server password")
    private String serverPassword = DEFAULT_SERVER_PASSWORD;

    public ManagerCommon(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
        managementApiService.configureEndpoint(serverAddress, serverUsername, serverPassword);
    }

    /**
     * @param clazz the Class for which to build a client
     * @param <T>   the API interface
     * @return an API client for the given Class
     */
    public <T> T buildServerApiClient(Class<T> clazz) {
        return buildServerApiClient(clazz, ManagementApiVersion.UNSPECIFIED);
    }

    /**
     * @param clazz         the Class for which to build a client
     * @param serverVersion the server version
     * @param <T>           the API interface
     * @return an API client for the given Class
     */
    public <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion) {
        return managementApiService.buildServerApiClient(
                clazz,
                serverVersion,
                getManagementApiEndpoint(),
                getManagementApiUsername(),
                getManagementApiPassword(),
                true);
    }

    public String getManagementApiEndpoint() {
        // TODO consider reading from config file/environment
        return serverAddress;
    }

    public String getManagementApiUsername() {
        // TODO consider reading from config file/environment
        return serverUsername;
    }

    public String getManagementApiPassword() {
        // TODO consider reading from config file/environment
        return serverPassword;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
