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

package io.apiman.cli;

import io.apiman.cli.command.AbstractCommand;
import io.apiman.cli.command.Command;
import io.apiman.cli.core.api.command.ApiCommand;
import io.apiman.cli.core.declarative.command.ManagerApplyCommand;
import io.apiman.cli.core.gateway.command.GatewayCommand;
import io.apiman.cli.core.org.command.OrgCommand;
import io.apiman.cli.core.plugin.command.PluginCommand;

import java.util.Map;

/**
 * The main class; the root of all Commands.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */

public class ManagerCli extends AbstractCommand {

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("org", OrgCommand.class);
        commandMap.put("gateway", GatewayCommand.class);
        commandMap.put("plugin", PluginCommand.class);
        commandMap.put("api", ApiCommand.class);
        commandMap.put("apply", ManagerApplyCommand.class);
    }

    @Override
    protected String getCommandDescription() {
        return "apiman-manager-cli";
    }

    @Override
    public String getCommandName() {
        return "manager";
    }
}
