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
package io.apiman.cli.gatewayapi.command.client;

import com.beust.jcommander.Parameters;
import io.apiman.cli.command.core.AbstractCommand;
import io.apiman.cli.command.core.Command;

import java.util.Map;

/**
 * Operations on client entities.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Retire and list Clients")
public class GatewayClientCommand extends AbstractCommand {
    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("unregister", UnregisterClientCommand.class);
        commandMap.put("list", ListClientCommand.class);
        commandMap.put("endpoint", ClientEndpointCommand.class);
    }
}
