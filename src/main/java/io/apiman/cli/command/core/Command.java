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

import io.apiman.cli.exception.CommandException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface Command {
    void setParent(Command command);

    void setCommandName(String command);

    String getCommandName();

    /**
     * Parse the given arguments and perform a command.
     *
     * @param args the arguments to parse
     */
    void run(List<String> args);

    /**
     * @return a concatenation of the parent's command name and this command name
     */
    String getCommandChain();

    /**
     * Default implementation will print usage and exit with an error code.
     * Subclasses should implement their custom behaviour here.
     *
     * @param parser the command line parser
     */
    void performAction(CmdLineParser parser) throws CommandException;
}
