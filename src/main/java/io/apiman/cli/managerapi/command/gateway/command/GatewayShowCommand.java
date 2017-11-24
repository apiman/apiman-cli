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
import com.beust.jcommander.Parameters;
import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.common.command.ModelShowCommand;
import io.apiman.cli.managerapi.command.gateway.GatewayApi;
import io.apiman.cli.managerapi.command.gateway.GatewayMixin;
import io.apiman.cli.managerapi.service.ManagementApiService;

import javax.inject.Inject;

/**
 * Show a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Show a gateway")
public class GatewayShowCommand extends ModelShowCommand<Gateway, GatewayApi>
        implements GatewayMixin {

    @Parameter(names = { "--name", "-n"}, description = "Name")
    private String name;

    @Inject
    public GatewayShowCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }


    @Override
    protected String getModelId() throws CommandException {
        return name;
    }
}
