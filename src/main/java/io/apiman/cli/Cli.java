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

import com.google.common.collect.Lists;
import io.apiman.cli.action.AbstractAction;
import io.apiman.cli.action.Action;
import io.apiman.cli.core.gateway.action.GatewayAction;
import io.apiman.cli.core.org.action.OrgAction;
import io.apiman.cli.core.plugin.action.PluginAction;
import io.apiman.cli.core.api.action.ApiAction;

import java.util.Map;

/**
 * The main class; the root of all Actions.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Cli extends AbstractAction {
    public static void main(String... args) {
        new Cli().run(Lists.newArrayList(args));
    }

    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("org", OrgAction.class);
        actionMap.put("gateway", GatewayAction.class);
        actionMap.put("plugin", PluginAction.class);
        actionMap.put("api", ApiAction.class);
    }

    @Override
    protected String getActionName() {
        return "apiman-cli";
    }

    @Override
    public String getCommand() {
        return "apiman";
    }
}
