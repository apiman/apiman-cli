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

package io.apiman.cli.managerapi.service;


import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.services.WaitService;

/**
 * Constructs management API clients.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface ManagementApiService extends WaitService {
    /**
     * Configures the server's management API endpoint.
     *
     * @param serverAddress  the URL of the management API
     * @param serverUsername management API username
     * @param serverPassword management API password
     */
    void configureEndpoint(String serverAddress, String serverUsername, String serverPassword);

    /**
     * @param clazz the Class for which to build a client
     * @param <T>   the API interface
     * @return an API client for the given Class
     */
    <T> T buildServerApiClient(Class<T> clazz);

    /**
     * @param clazz         the Class for which to build a client
     * @param serverVersion the server version
     * @param <T>           the API interface
     * @return an API client for the given Class
     */
    <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion);

    /**
     * @param <T>           the API interface
     * @param clazz         the Class for which to build a client
     * @param serverVersion the server version
     * @param username      the management API username
     * @param password      the management API password
     * @param debugLogging  whether debug logging should be enabled
     * @return an API client for the given Class
     */
    <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion, String endpoint, String username,
                               String password, boolean debugLogging);
}
