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

package io.apiman.cli.managerapi.command.common.util;

import io.apiman.cli.managerapi.command.common.ActionApi;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.command.common.model.ServerAction;
import io.apiman.cli.managerapi.management.ManagementApiUtil;

import java.net.HttpURLConnection;

/**
 * Common server action functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ServerActionUtil {
    /**
     * Publish an API, taking into account the Management Server API version.
     *
     * @param orgName       the organisation name
     * @param apiName       the API name
     * @param apiVersion    the API version
     * @param serverVersion the Management Server API version
     * @param apiClient     the Server Action API client
     */
    public static void publishApi(String orgName, String apiName, String apiVersion,
                                  ManagementApiVersion serverVersion, ActionApi apiClient) {
        String actionType;
        switch (serverVersion) {
            case v11x:
                // legacy apiman 1.1.x support
                actionType = "publishService";
                break;

            default:
                // apiman 1.2.x support
                actionType = "publishAPI";
                break;
        }

        ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_NO_CONTENT, () -> {
            final ServerAction action = new ServerAction(
                    actionType,
                    orgName,
                    apiName,
                    apiVersion
            );

            return apiClient.doAction(action);
        });
    }
    
    /**
     * Lock a Plan.
     *
     * @param orgName       the organisation name
     * @param planName       the plan name
     * @param planVersion    the plan version
     * @param actionClient     the Server Action Plan client
     */
    public static void lockPlan(String orgName, String planName, String planVersion, ActionApi actionClient) {
        String actionType = "lockPlan";

        ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_NO_CONTENT, () -> {
            final ServerAction action = new ServerAction(
                    actionType,
                    orgName,
                    planName,
                    planVersion
            );

            return actionClient.doAction(action);
        });
    }
    
    /**
     * Register a Client.
     *
     * @param orgName       the organisation name
     * @param clientName       the client name
     * @param clientVersion    the client version
     * @param actionClient     the Server Action API client
     */
    public static void registerClient(String orgName, String clientName, String clientVersion,
                                      ManagementApiVersion serverVersion, ActionApi actionClient) {
        String actionType;
        switch (serverVersion) {
            case v11x:
                // legacy apiman 1.1.x support
                actionType = "registerApplication";
                break;

            default:
                // apiman 1.2.x support
                actionType = "registerClient";
                break;
        }
        ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_NO_CONTENT, () -> {
            final ServerAction action = new ServerAction(
                    actionType,
                    orgName,
                    clientName,
                    clientVersion
            );

            return actionClient.doAction(action);
        });
    }
}
