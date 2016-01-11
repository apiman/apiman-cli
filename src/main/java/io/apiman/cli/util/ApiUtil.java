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

import com.google.common.io.CharStreams;
import io.apiman.cli.exception.ActionException;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

/**
 * Shared API utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiUtil {
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
}
