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

package io.apiman.cli.core.gateway.action;

import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.model.GatewayTestResponse;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.MessageFormat;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * Test a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class GatewayTestAction extends AbstractGatewayCreateAction {
    private static final Logger LOGGER = LogManager.getLogger(GatewayTestAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = false)
    private String name;

    @Override
    protected String getActionName() {
        return MessageFormat.format("Test {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Testing {}", this::getModelName);

        GatewayTestResponse response;
        try {
            final GatewayApi apiClient = buildApiClient(GatewayApi.class);
            response = apiClient.test(buildModelInstance());

            OUTPUT.info("Test {}", () -> response.isSuccess() ? "successful" : "failed");
            LOGGER.debug("Test result: {}", () -> MappingUtil.safeWriteValueAsJson(response));

        } catch (Exception e) {
            throw new ActionException(e);
        }

        if (!response.isSuccess()) {
            throw new ExitWithCodeException(1, MessageFormat.format("Test failed: {0}", response.getDetail()));
        }
    }

    @Override
    protected String getGatewayName() {
        return null;
    }
}
