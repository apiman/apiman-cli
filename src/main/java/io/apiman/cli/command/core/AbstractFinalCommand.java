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

package io.apiman.cli.command.core;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.Map;

/**
 * A Command that has no child Commands.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractFinalCommand extends AbstractCommand {
    private static final Integer DEFAULT_WAIT_TIME = 0;

    @Parameter(names = {"--waitTime", "-w"}, description = "Server startup wait time (seconds)")
    private Integer waitTime = DEFAULT_WAIT_TIME;

    /**
     * Indicates that there is no child command and that this instance should handle the request.
     *
     * @param args
     * @param parser
     * @return <code>null</code>
     */
    protected Command getChildAction(String commandName, JCommander parser) {
        return null;
    }

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        // no child commands
    }
}
