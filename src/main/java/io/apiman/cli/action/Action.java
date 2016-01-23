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

package io.apiman.cli.action;

import io.apiman.cli.exception.ActionException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public interface Action {
    void setParent(Action action);

    void setCommand(String command);

    String getCommand();

    /**
     * Parse the given arguments and perform an action.
     *
     * @param args the arguments to parse
     */
    void run(List<String> args);

    /**
     * @return a concatenation of the parent's action command and this action command
     */
    String getCommandChain();

    /**
     * Default implementation will print usage and exit with an error code.
     * Subclasses should implement their custom behaviour here.
     *
     * @param parser the command line parser
     */
    void performAction(CmdLineParser parser) throws ActionException;
}
