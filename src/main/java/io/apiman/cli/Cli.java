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

package io.apiman.cli;

import com.google.common.collect.Lists;
import io.apiman.cli.command.api.command.ApiCommand;
import io.apiman.cli.command.core.AbstractCommand;
import io.apiman.cli.command.core.Command;
import io.apiman.cli.command.declarative.command.ApplyCommand;
import io.apiman.cli.command.gateway.command.GatewayCommand;
import io.apiman.cli.command.org.command.OrgCommand;
import io.apiman.cli.command.plugin.command.PluginCommand;
import io.apiman.cli.service.ManagementApiService;
import io.apiman.cli.util.InjectionUtil;

import javax.inject.Inject;
import java.util.Map;

/**
 * The main class; the root of all Commands.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Cli extends AbstractCommand {
    @Inject
    public Cli(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    public static void main(String... args) {
        InjectionUtil.getInjector().getInstance(Cli.class).run(Lists.newArrayList(args));
    }

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("org", OrgCommand.class);
        commandMap.put("gateway", GatewayCommand.class);
        commandMap.put("plugin", PluginCommand.class);
        commandMap.put("api", ApiCommand.class);
        commandMap.put("apply", ApplyCommand.class);
    }

    @Override
    protected String getCommandDescription() {
        return "apiman-cli";
    }

    @Override
    public String getCommandName() {
        return "apiman";
    }
}
