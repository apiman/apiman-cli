/*
 * Copyright 2017 Red Hat, Inc.
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

package io.apiman.cli.gatewayapi;

import io.apiman.cli.exception.CommandException;
import io.apiman.gateway.api.rest.contract.exceptions.GatewayApiErrorBean;
import io.apiman.gateway.engine.beans.SystemStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.RetrofitError;

import java.util.function.Supplier;

import static java.text.MessageFormat.format;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public interface GatewayHelper {
    Logger LOGGER = LogManager.getLogger(GatewayHelper.class);
    long STATUS_CHECK_INTERVAL = 1000;

    default boolean statusCheck(GatewayApi client, String endpoint) {
        SystemStatus status = callAndCatch(client::getSystemStatus);
        LOGGER.debug("Gateway status: {}", status);
        if (!status.isUp()) {
            throw new StatusCheckException(endpoint, "Status indicates gateway is currently down");
        }
        return status.isUp();
    }

    default <T> T callAndCatch(Supplier<T> action) {
        try {
            return action.get();
        } catch(RetrofitError e) {
            String endpoint = e.getUrl();
            LOGGER.debug("Endpoint: {}, RetrofitError: {}", endpoint, e);
            switch (e.getKind()) {
                case NETWORK:
                    throw new StatusCheckException(endpoint, "Network issue: " + e.getMessage());
                case CONVERSION:
                    throw e;
                case HTTP:
                    GatewayApiErrorBean errorBean = null;
                    String message;

                    try {
                        errorBean = (GatewayApiErrorBean) e.getBodyAs(GatewayApiErrorBean.class);
                    } catch (Exception ignored) {}

                    // If the error body was an GatewayApiErrorBean
                    if (errorBean != null) {
                        message = format("Apiman Error: {0} {1}", errorBean.getErrorType(), errorBean.getMessage());
                        if (LOGGER.isDebugEnabled()) {
                            System.err.println(errorBean.getStacktrace());
                        }
                    } else { // Otherwise just use plain HTTP reason.
                        message = e.getResponse().getReason();
                    }

                    throw new StatusCheckException(endpoint,
                            format("Unsuccessful response code: {0}. {1}",
                                    e.getResponse().getStatus(),
                                    message));
                case UNEXPECTED:
                    throw new StatusCheckException(endpoint, format("Unexpected exception: {0}", e.getMessage()));
            }
            throw e;
        }
    }

    default void waitForServer(GatewayApi apiClient, int waitTime) {
        if (waitTime == 0) {
            return;
        }

        LOGGER.info("Waiting {} seconds for server to start...", waitTime);

        final long start = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - start > waitTime * 1000) {
                throw new CommandException("Timed out after " + waitTime + " seconds waiting for server to start");
            }

            try {
                final SystemStatus response = apiClient.getSystemStatus();
                if (response.isUp()) {
                    LOGGER.info("Server started");
                    break;
                }
            } catch (RetrofitError rfe) {
                try {
                    Thread.sleep(STATUS_CHECK_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    class StatusCheckException extends CommandException {

        StatusCheckException(String endpoint, String message) {
            super(format("Status check failed on gateway {0}. {1}",
                    endpoint,
                    message));
        }
    }
}
