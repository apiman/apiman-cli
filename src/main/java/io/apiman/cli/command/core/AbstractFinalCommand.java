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
import io.apiman.cli.annotations.CommandAvailableSince;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.services.WaitService;

import java.util.Map;

/**
 * A Command that has no child Commands.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractFinalCommand extends AbstractCommand {
    private static final Integer DEFAULT_WAIT_TIME = 0;
    private final WaitService waitService;

    @Parameter(names = {"--waitTime", "-w"}, description = "Server startup wait time (seconds)")
    private Integer waitTime = DEFAULT_WAIT_TIME;

    protected AbstractFinalCommand(WaitService waitService) {
        this.waitService = waitService;
    }

    /**
     * If you don't need to wait for a remote server or if more flexibility is required
     * (e.g. multiple servers), this constructor provides a no-op wait service
     */
    protected AbstractFinalCommand() {
        waitService = (int ignored) -> {};
    }

    public final void performAction(JCommander parser) throws CommandException {
        waitService.waitForServer(waitTime);
        doVersionCheck();
        performFinalAction(parser);
    }

    public abstract void performFinalAction(JCommander parser) throws CommandException;

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

    /**
     * Allows subclass to perform a remote version check to determine whether command will work
     *
     * @param availableSince version number indicating availability of functionality
     */
    protected void versionCheck(String availableSince) {
        // No version check unless overridden.
    }

    private void doVersionCheck() {
        CommandAvailableSince since = this.getClass().getAnnotation(CommandAvailableSince.class);
        if (since != null) {
            versionCheck(since.value());
        }
    }

}
