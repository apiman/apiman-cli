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

package io.apiman.cli.command.plugin.command;

import io.apiman.cli.command.common.command.ModelShowCommand;
import io.apiman.cli.command.plugin.PluginApi;
import io.apiman.cli.command.plugin.PluginMixin;
import io.apiman.cli.command.plugin.model.Plugin;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.service.ManagementApiService;
import org.kohsuke.args4j.Option;

import javax.inject.Inject;

/**
 * Show a plugin.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class PluginShowCommand extends ModelShowCommand<Plugin, PluginApi> implements PluginMixin {
    @Option(name = "--id", aliases = {"-i"}, usage = "Plugin ID")
    private String id;

    @Inject
    public PluginShowCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getModelId() throws CommandException {
        return id;
    }
}
