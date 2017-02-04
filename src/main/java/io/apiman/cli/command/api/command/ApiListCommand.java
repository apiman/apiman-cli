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

package io.apiman.cli.command.api.command;

import io.apiman.cli.command.api.ApiMixin;
import io.apiman.cli.command.api.VersionAgnosticApi;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.List;

/**
 * List APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiListCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiListCommand.class);

    @Inject
    public ApiListCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        LOGGER.debug("Listing {}", this::getModelName);

        final List<Api> apis = buildServerApiClient(VersionAgnosticApi.class, serverVersion).list(orgName);
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(apis));
    }
}
