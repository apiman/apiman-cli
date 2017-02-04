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

import io.apiman.cli.command.common.command.ModelShowCommand;
import io.apiman.cli.command.gateway.GatewayApi;
import io.apiman.cli.command.gateway.GatewayMixin;
import io.apiman.cli.command.gateway.model.Gateway;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.service.ManagementApiService;
import org.kohsuke.args4j.Option;

import javax.inject.Inject;

/**
 * Show a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class GatewayShowCommand extends ModelShowCommand<Gateway, GatewayApi> implements GatewayMixin {
    @Option(name = "--name", aliases = {"-n"}, usage = "Name")
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
