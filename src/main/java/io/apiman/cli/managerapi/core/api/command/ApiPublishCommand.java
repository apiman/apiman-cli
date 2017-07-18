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

package io.apiman.cli.managerapi.core.api.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.managerapi.core.api.ApiMixin;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.managerapi.core.common.util.ServerActionUtil;
import io.apiman.cli.exception.CommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Publish an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Publish API")
public class ApiPublishCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiPublishCommand.class);

    @Parameter(names = { "--name", "-n"}, description = "API name", required = true)
    private String name;

    @Parameter(names = { "--version", "-v"}, description = "API version", required = true)
    private String version;

    @Override
    public void performAction(JCommander parser) throws CommandException {
        LOGGER.debug("Publishing {}", this::getModelName);
        ServerActionUtil.publishApi(orgName, name, version, serverVersion, getManagerConfig().buildServerApiClient(ActionApi.class));
    }

}
