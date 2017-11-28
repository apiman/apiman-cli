/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.management;

import io.apiman.cli.core.common.model.ManagementApiVersion;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.management.binding.ManagementApiBindings;
import io.apiman.cli.management.factory.ManagementApiFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.CharStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Shared Management API utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiUtil {
    private static final Logger LOGGER = LogManager.getLogger(ManagementApiUtil.class);
    private static boolean factoriesInitialised;
    private static Injector apiFactories;

    public static void invokeAndCheckResponse(Supplier<Response> request) throws CommandException {
        invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
    }

    public static void invokeAndCheckResponse(int expectedStatus, Supplier<Response> request) throws CommandException {
        try {
            // invoke the request
            final Response response = request.get();

            // check response code is successful
            if (response.getStatus() != expectedStatus) {
                httpError(expectedStatus, response);
            }

        } catch (RetrofitError e) {
            httpError(expectedStatus, e.getResponse());
        }
    }

    private static void httpError(int expectedStatus, Response response) throws CommandException {
        if (null == response) {
            throw new IllegalArgumentException("Response was null");
        }

        // obtain response body
        String body = null;

        if (null != response.getBody()) {
            try (InputStream errStream = response.getBody().in()) {
                body = CharStreams.toString(new InputStreamReader(errStream));
            } catch (IOException ignored) {
            }
        }

        throw new CommandException("HTTP " + response.getStatus() + " "
                + response.getReason() + " but expected " + expectedStatus + ":\n" + body);
    }

    /**
     * @param clazz         the Class for which to build a client
     * @param username      the management API username
     * @param password      the management API password
     * @param debugLogging  whether debug logging should be enabled
     * @param serverVersion the server version
     * @param <T>           the API interface
     * @return an API client for the given Class
     */
    @SuppressWarnings("unchecked")
    public static <T> T buildServerApiClient(Class<T> clazz, String endpoint, String username,
                                             String password, boolean debugLogging, ManagementApiVersion serverVersion) {
        if (!factoriesInitialised) {
            LOGGER.trace("Initialising API factories");
            apiFactories = Guice.createInjector(new ManagementApiFactoryModule());
            factoriesInitialised = true;
        }
        // locate the Management API factory
        final ManagementApiFactory managementApiFactory;
        try {
            managementApiFactory = apiFactories.getInstance(
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
}
