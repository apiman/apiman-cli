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

package io.apiman.cli.managerapi.management;

import com.google.common.io.CharStreams;
import io.apiman.cli.exception.CommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Shared Management API utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiUtil {
    private static final Logger LOGGER = LogManager.getLogger(ManagementApiUtil.class);

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
     * Check for the presence of an item using the given Supplier.
     *
     * @param supplier the Supplier of the item
     * @param <T>
     * @return the item or {@link Optional#empty()}
     */
    public static <T> Optional<T> checkExists(Supplier<T> supplier) {
        try {
            // attempt to return the item
            return ofNullable(supplier.get());

        } catch (RetrofitError re) {
            // 404 indicates the item does not exist - anything else is an error
            if (ofNullable(re.getResponse())
                    .filter(response -> HttpURLConnection.HTTP_NOT_FOUND == response.getStatus())
                    .isPresent()) {

                return empty();
            }

            throw new CommandException("Error checking for existence of existing item", re);
        }
    }
}
