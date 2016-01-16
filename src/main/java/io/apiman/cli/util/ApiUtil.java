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

package io.apiman.cli.util;

import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import io.apiman.cli.exception.ActionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

import static io.apiman.cli.util.MappingUtil.JSON_MAPPER;

/**
 * Shared API utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiUtil {
    private static final Logger LOGGER = LogManager.getLogger(ApiUtil.class);

    public static void invokeAndCheckResponse(Supplier<Response> request) throws ActionException {
        invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
    }

    public static void invokeAndCheckResponse(int expectedStatus, Supplier<Response> request) throws ActionException {
        final Response response;
        try {
            // invoke the request
            response = request.get();

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
     * @param clazz        the Class for which to build a client
     * @param username     the management API username
     * @param password     the management API password
     * @param debugLogging whether debug logging should be enabled
     * @param <T>          the API interface
     * @return an API client for the given Class
     */
    public static <T> T buildApiClient(Class<T> clazz, String endpoint, String username, String password, boolean debugLogging) {
        final RestAdapter.Builder builder = new RestAdapter.Builder() //
                .setConverter(new JacksonConverter(JSON_MAPPER))
                .setEndpoint(endpoint)
                .setRequestInterceptor(request -> {
                    final String credentials = String.format("%s:%s", username, password);
                    request.addHeader("Authorization", "Basic " + BaseEncoding.base64().encode(credentials.getBytes()));
                });

        if (debugLogging) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(clazz);
    }
}
