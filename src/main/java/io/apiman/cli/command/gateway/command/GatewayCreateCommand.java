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

import io.apiman.cli.service.ManagementApiService;
import org.kohsuke.args4j.Option;

import javax.inject.Inject;

/**
 * Create a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class GatewayCreateCommand extends AbstractGatewayCreateCommand {
    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
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
