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

package io.apiman.cli.command.gateway.command;

import io.apiman.cli.command.gateway.GatewayApi;
import io.apiman.cli.command.gateway.model.GatewayTestResponse;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import javax.inject.Inject;
import java.text.MessageFormat;

/**
 * Test a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class GatewayTestCommand extends AbstractGatewayCreateCommand {
    private static final Logger LOGGER = LogManager.getLogger(GatewayTestCommand.class);

    @Inject
    public GatewayTestCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("Test {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        LOGGER.debug("Testing {}", this::getModelName);

        GatewayTestResponse response;
        try {
            final GatewayApi apiClient = buildServerApiClient(GatewayApi.class);
            response = apiClient.test(buildModelInstance());

            LogUtil.OUTPUT.info("Test {}", () -> response.isSuccess() ? "successful" : "failed");
            LOGGER.debug("Test result: {}", () -> MappingUtil.safeWriteValueAsJson(response));

        } catch (Exception e) {
            throw new CommandException(e);
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
