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
import io.apiman.cli.exception.CommandException;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface Command {

    /**
     * If the command is a child, the parent.
     *
     * @param command the parent command.
     */
    void setParent(Command command);

    /**
     * Name of the command
     *
     * @param command the command name
     */
    void setCommandName(String command);

    /**
     * Parse the given arguments and perform a command.
     *
     * @param args the arguments to parse
     */
    void run(List<String> args, JCommander jcommander);

    /**
     * Default implementation will print usage and exit with an error code.
     * Subclasses should implement their custom behaviour here.
     *
     * @param parser the command line parser
     */
    void performAction(JCommander parser) throws CommandException;

    /**
     * Build subcommands and instantiate any children.
     *
     * @param jcommander the jcommander instance
     */
    void build(JCommander jcommander);

}
