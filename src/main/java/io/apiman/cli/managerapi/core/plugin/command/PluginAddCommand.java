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

package io.apiman.cli.managerapi.core.plugin.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.managerapi.command.ModelCreateCommand;
import io.apiman.cli.managerapi.core.plugin.PluginApi;
import io.apiman.cli.managerapi.core.plugin.PluginMixin;
import io.apiman.cli.managerapi.core.plugin.model.Plugin;
import io.apiman.cli.exception.CommandException;

/**
 * Add a plugin.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Add a plugin")
public class PluginAddCommand extends ModelCreateCommand<Plugin, PluginApi>
        implements PluginMixin {

    @Parameter(names = {"--groupId", "-g"}, description = "Group ID", required = true)
    private String groupId;

    @Parameter(names = {"--artifactId", "-a"}, description = "Artifact ID", required = true)
    private String artifactId;

    @Parameter(names = {"--version", "-v"}, description = "Version", required = true)
    private String version;

    @Parameter(names = {"--classifier", "-c"}, description = "Classifier")
    private String classifier;

    @Override
    protected Plugin buildModelInstance() throws CommandException {
        return new Plugin(groupId, artifactId, classifier, version);
    }
}
