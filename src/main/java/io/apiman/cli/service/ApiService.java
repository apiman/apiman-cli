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

package io.apiman.cli.service;

import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;

/**
 * Manages APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface ApiService {
    String STATE_READY = "READY";
    String STATE_PUBLISHED = "PUBLISHED";
    String STATE_RETIRED = "RETIRED";

    /**
     * Return the current state of the API.
     *
     * @param serverVersion the management server API version
     * @param orgName       the organisation name
     * @param apiName       the API name
     * @param apiVersion    the API version
     * @return the API state
     */
    String fetchCurrentState(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion);

    /**
     * Publish the API, if it is in the 'Ready' state.
     *
     * @param serverVersion the management server API version
     * @param orgName       the organisation name
     * @param apiName       the API name
     * @param apiVersion    the API version
     */
    void publish(ManagementApiVersion serverVersion, String orgName, String apiName, String apiVersion);
}
