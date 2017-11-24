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

package io.apiman.cli.managerapi.command.gateway.command;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.command.gateway.model.GatewayConfig;
import io.apiman.cli.command.gateway.model.GatewayType;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.common.command.ModelCreateCommand;
import io.apiman.cli.managerapi.command.gateway.GatewayApi;
import io.apiman.cli.managerapi.command.gateway.GatewayMixin;
import io.apiman.cli.managerapi.service.ManagementApiService;
import io.apiman.cli.util.MappingUtil;

/**
 * Shared functionality for commands requiring a {@link Gateway}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractGatewayCreateCommand extends ModelCreateCommand<Gateway, GatewayApi>
        implements GatewayMixin {

    @Parameter(names = { "--description", "-d"}, description = "Description")
    private String description;

    @Parameter(names = { "--endpoint", "-e"}, description = "Endpoint", required = true)
    private String endpoint;

    @Parameter(names = { "--username", "-u"}, description = "Username")
    private String username;

    @Parameter(names = { "--password", "-p"}, description = "Password")
    private String password;

    @Parameter(names = { "--type", "-t"}, description = "type")
    private GatewayType type = GatewayType.REST;

    public AbstractGatewayCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected Gateway buildModelInstance() throws CommandException {
        final String config;
        try {
            config = MappingUtil.JSON_MAPPER.writeValueAsString(
                    new GatewayConfig(endpoint,
                            username,
                            password));

        } catch (JsonProcessingException e) {
            throw new CommandException(e);
        }

        return new Gateway(getGatewayName(),
                description,
                type,
                config);
    }

    protected abstract String getGatewayName();
}
