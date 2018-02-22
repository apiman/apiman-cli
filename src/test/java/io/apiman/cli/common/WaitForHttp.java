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

package io.apiman.cli.common;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.apiman.cli.util.AuthUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.rnorth.ducttape.TimeoutException;
import org.rnorth.ducttape.ratelimits.RateLimiter;
import org.rnorth.ducttape.ratelimits.RateLimiterBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.rnorth.ducttape.unreliables.Unreliables.retryUntilSuccess;

/**
 * This class rule waits for an HTTP endpoint to return a 200 status.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class WaitForHttp implements TestRule {
    private static final Logger LOGGER = LogManager.getLogger(WaitForHttp.class);

    private static final RateLimiter RATE_LIMITER = RateLimiterBuilder
            .newBuilder()
            .withRate(1, TimeUnit.SECONDS)
            .withConstantThroughput()
            .build();

    /**
     * Start time in seconds.
     */
    private static final int START_TIMEOUT = 120;

    /**
     * The status code to expect.
     */
    private int statusCode = HttpURLConnection.HTTP_OK;

    private String host;
    private int port;
    private final String path;
    private String username;
    private String password;

    public WaitForHttp(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final List<Throwable> errors = Lists.newArrayList();

                starting();

                try {
                    base.evaluate();
                } catch (Throwable e) {
                    errors.add(e);
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    protected void starting() {
        final String url = getAddress() + path;
        LOGGER.info("Waiting for URL: {}", url);

        // try to connect to the URL
        try {
            retryUntilSuccess(START_TIMEOUT, TimeUnit.SECONDS, () -> {
                RATE_LIMITER.doWhenReady(() -> {
                    try {
                        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                        // authenticate
                        if (!Strings.isNullOrEmpty(username)) {
                            connection.setRequestProperty(AuthUtil.HEADER_AUTHORIZATION, AuthUtil.buildAuthString(username, password));
                            connection.setUseCaches(false);
                        }

                        connection.setRequestMethod("GET");
                        connection.connect();

                        if (statusCode != connection.getResponseCode()) {
                            throw new RuntimeException(String.format("HTTP response code was: %s",
                                    connection.getResponseCode()));
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                return true;
            });

        } catch (TimeoutException e) {
            throw new RuntimeException(String.format(
                    "Timed out waiting for URL to be accessible (%s should return HTTP %s)", url, statusCode));
        }
    }

    public String getAddress() {
        return String.format("http://%s:%s", host, port);
    }

    /**
     * @param statusCode the status code to expect
     * @return this
     */
    public WaitForHttp withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Authenticate with HTTP basic auth credentials.
     *
     * @param username the username
     * @param password the password
     * @return this
     */
    public WaitForHttp withBasicCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }
}