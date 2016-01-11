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

package io.apiman.cli.core.plugin.action;

import io.apiman.cli.core.common.action.ModelShowAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.core.plugin.PluginMixin;
import io.apiman.cli.core.plugin.model.Plugin;
import org.kohsuke.args4j.Option;

/**
 * Show a plugin.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class PluginShowAction extends ModelShowAction<Plugin, PluginApi>
        implements PluginMixin {

    @Option(name = "--id", aliases = {"-i"}, usage = "Plugin ID")
    private String id;

    @Override
    protected String getModelId() throws ActionException {
        return id;
    }
}
