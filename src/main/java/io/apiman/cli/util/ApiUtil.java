package io.apiman.cli.util;

import com.google.common.io.CharStreams;
import io.apiman.cli.api.exception.ActionException;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.function.Supplier;

/**
 * @author Pete
 */
public class ApiUtil {
    public static void invokeAndCheckResponse(Supplier<Response> request) throws ActionException {
        invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
    }

    protected static void invokeAndCheckResponse(int expectedStatus, Supplier<Response> request) throws ActionException {
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
        // obtain response body
        String body = null;
        try (InputStream errStream = response.getBody().in()) {
            body = CharStreams.toString(new InputStreamReader(errStream));
        } catch (IOException ignored) {
        }

        throw new ActionException("HTTP " + response.getStatus() + " "
                + response.getReason() + " but expected " + expectedStatus + ":\n" + body);
    }
}
