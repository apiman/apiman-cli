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

package io.apiman.cli.managerapi.core.gateway.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.service.ManagementApiService;

import javax.inject.Inject;

/**
 * Create a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Create a new gateway")
public class GatewayCreateCommand extends AbstractGatewayCreateCommand {

    @Parameter(names = {"--name", "-n"}, description = "Name", required = true)
    private String name;

    @Inject
    public GatewayCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getGatewayName() {
        return name;
    }
}
