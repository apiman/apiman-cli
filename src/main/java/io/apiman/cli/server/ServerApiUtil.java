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

package io.apiman.cli.server;

import com.google.common.io.CharStreams;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.apiman.cli.core.common.model.ServerVersion;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.server.binding.ServerApiBindings;
import io.apiman.cli.server.factory.ServerApiFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

/**
 * Shared Server API utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ServerApiUtil {
    private static final Logger LOGGER = LogManager.getLogger(ServerApiUtil.class);
    private static boolean factoriesInitialised;
    private static Injector apiFactories;

    public static void invokeAndCheckResponse(Supplier<Response> request) throws ActionException {
        invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
    }

    public static void invokeAndCheckResponse(int expectedStatus, Supplier<Response> request) throws ActionException {
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

    private static void httpError(int expectedStatus, Response response) throws ActionException {
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

        throw new ActionException("HTTP " + response.getStatus() + " "
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
                                             String password, boolean debugLogging, ServerVersion serverVersion) {

        if (!factoriesInitialised) {
            factoriesInitialised = true;

            LOGGER.trace("Initialising API factories for server version {}", serverVersion);
            apiFactories = Guice.createInjector(new ServerApiFactoryModule());
        }

        // locate the server API factory
        final ServerApiFactory serverApiFactory = apiFactories.getInstance(
                Key.get(ServerApiFactory.class, ServerApiBindings.boundTo(clazz, serverVersion)));

        // use the factory to construct the server API client
        return (T) serverApiFactory.build(endpoint, username, password, debugLogging);
    }
}
