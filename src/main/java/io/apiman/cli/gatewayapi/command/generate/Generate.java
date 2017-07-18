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
package io.apiman.cli.gatewayapi.command.generate;

import com.beust.jcommander.Parameters;
import io.apiman.cli.command.AbstractCommand;
import io.apiman.cli.command.Command;

import java.util.Map;

/**
 * Sub-command for generating gateway configurations
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Generate configurations")
public class Generate extends AbstractCommand {
    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("headless", GenerateHeadless.class); // Is this generic enough?
    }
}
