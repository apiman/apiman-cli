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

import com.google.inject.Key;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.management.api.StatusApi;
import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.management.binding.ManagementApiBindings;
import io.apiman.cli.managerapi.management.factory.ManagementApiFactory;
import io.apiman.cli.util.InjectionUtil;
import io.apiman.cli.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.client.Response;

import java.net.HttpURLConnection;

/**
 * Constructs management API clients.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiServiceImpl implements ManagementApiService {
    private static final Logger LOGGER = LogManager.getLogger(ManagementApiServiceImpl.class);

    /**
     * Interval, in milliseconds, that the management API's status will be polled, whilst waiting
     * for the server to be ready.
     *
     * @see #waitForServer(int)
     */
    private static final long STATUS_CHECK_INTERVAL = 1000;

    private String serverAddress;
    private String serverUsername;
    private String serverPassword;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureEndpoint(String serverAddress, String serverUsername, String serverPassword) {
        this.serverAddress = serverAddress;
        this.serverUsername = serverUsername;
        this.serverPassword = serverPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T buildServerApiClient(Class<T> clazz) {
        return buildServerApiClient(clazz, ManagementApiVersion.UNSPECIFIED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion) {
        return buildServerApiClient(
                clazz,
                serverVersion, serverAddress,
                serverUsername,
                serverPassword,
                LogUtil.isLogDebug()
        );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion,
                                      String endpoint, String username, String password, boolean debugLogging) {

        // locate the Management API factory
        final ManagementApiFactory managementApiFactory;
        try {
            managementApiFactory = InjectionUtil.getInjector().getInstance(
                    Key.get(ManagementApiFactory.class, ManagementApiBindings.boundTo(clazz, serverVersion)));

        } catch (Exception e) {
            throw new CommandException(String.format(
                    "Error locating API factory for %s, with server version %s", clazz, serverVersion), e);
        }

        LOGGER.debug("Located API factory {} for {}, with server version {}",
                managementApiFactory.getClass(), clazz, serverVersion);

        // use the factory to construct the Management API client
        return (T) managementApiFactory.build(endpoint, username, password, debugLogging);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForServer(int waitTime) {
        if (waitTime == 0) {
            return;
        }

        LOGGER.info("Waiting {} seconds for server to start...", waitTime);
        final StatusApi apiClient = buildServerApiClient(StatusApi.class);

        final long start = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - start > waitTime * 1000) {
                throw new CommandException("Timed out after " + waitTime + " seconds waiting for server to start");
            }

            try {
                final Response response = apiClient.checkStatus();
                if (HttpURLConnection.HTTP_OK == response.getStatus()) {
                    LOGGER.info("Server started");
                    break;
                }

                Thread.sleep(STATUS_CHECK_INTERVAL);
            } catch (Exception ignored) {
            }
        }
    }
}
