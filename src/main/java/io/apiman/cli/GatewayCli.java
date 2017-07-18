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

package io.apiman.cli;

import com.beust.jcommander.Parameters;
import io.apiman.cli.command.AbstractCommand;
import io.apiman.cli.command.Command;
import io.apiman.cli.gatewayapi.command.GatewayOrgCommand;
import io.apiman.cli.gatewayapi.command.GatewayStatusCommand;
import io.apiman.cli.gatewayapi.command.api.GatewayApiCommand;
import io.apiman.cli.gatewayapi.command.client.GatewayClientCommand;
import io.apiman.cli.gatewayapi.command.generate.Generate;
import io.apiman.cli.gatewayapi.declarative.command.GatewayApplyCommand;

import java.util.Map;

/**
 * Root of Gateway commands.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Interact with an Apiman Gateway directly")
public class GatewayCli extends AbstractCommand {

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("generate", Generate.class);
        commandMap.put("apply", GatewayApplyCommand.class);
        commandMap.put("org", GatewayOrgCommand.class);
        commandMap.put("api", GatewayApiCommand.class);
        commandMap.put("client", GatewayClientCommand.class);
        commandMap.put("status", GatewayStatusCommand.class);
    }
}
