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
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.util.InjectionUtil;
import io.apiman.cli.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.apiman.cli.util.LogUtil.LINE_SEPARATOR;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(AbstractCommand.class);

    /**
     * Maps commands (e.g. 'org' or 'create') to their implementations.
     */
    private final Map<String, Class<? extends Command>> commandMap;
    private final Map<String, Command> commandInstanceMap;

    @Parameter(names = "--debug", description = "Log at DEBUG level")
    private boolean logDebug;

    @Parameter(names = {"--help", "-h"}, description = "Display usage only", help = true)
    private boolean displayHelp;

    /**
     * When a user provides an invalid flag with subcommands JCommander's errors are confusing and refer to a main
     * parameter, even when it's not used. A workaround is to add a main parameter and detect when invalid entries
     * land into it, throwing a custom error message instead.
     */
    @Parameter(hidden=true)
    private List<String> mainParameter = new ArrayList<>();

    /**
     * The parent Command (<code>null</code> if root).
     */
    private Command parent;

    /**
     * The name of this command.
     */
    private String commandName;

    /**
     * Guice injector.
     */
    private Injector injector = InjectionUtil.getInjector();

    public AbstractCommand() {
        // get child commands
        commandMap = Maps.newLinkedHashMap();
        commandInstanceMap = Maps.newHashMap();
        populateCommands(commandMap);
    }

    /**
     * Subclasses should populate the Map with their child Commands.
     *
     * @param commandMap the Map to populate
     */
    protected abstract void populateCommands(Map<String, Class<? extends Command>> commandMap);

    @Override
    public void setParent(Command parent) {
        this.parent = parent;
    }

    /**
     * @param commandName the name of this command
     */
    @Override
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    private static JCommander addSubCommand(JCommander parentCommand,
                                            String commandName, Object commandObject) {
        parentCommand.addCommand(commandName, commandObject);
        return parentCommand.getCommands().get(commandName);
    }

    @Override
    public void build(JCommander jc) {
        for (Map.Entry<String, Class<? extends Command>> entry : commandMap.entrySet()) {
            Command childAction = getChildAction(entry.getKey(), jc);
            commandInstanceMap.put(entry.getKey(), childAction);
            JCommander sub = addSubCommand(jc, entry.getKey(), childAction);
            childAction.build(sub);
        }
    }

    @Override
    public void run(List<String> args, JCommander jc) {
        LogUtil.configureLogging(logDebug);
        LOGGER.debug("Command Name: {}. Args: {} ", commandName, args);

        if (displayHelp) {
            printUsage(jc, true);
        }

        if (!mainParameter.isEmpty()) {
            ParameterException ex = new ParameterException("Unrecognised option(s): " +
                    mainParameter.stream().collect(Collectors.joining(", ")));
            ex.setJCommander(jc);
            throw ex;
        }

        Command childInstance = commandInstanceMap.get(jc.getParsedCommand());
        // If end of chain
        if (childInstance == null) {
            if (!permitNoArgs() && noArgsSet(jc)) {
                printUsage(jc, false);
            }
            performAction(jc);
        } else {
            JCommander subCommand = jc.getCommands().get(jc.getParsedCommand());
            childInstance.run(args, subCommand);
        }
    }

    private boolean noArgsSet(JCommander jc) {
        return jc.getParameters().stream().noneMatch(ParameterDescription::isAssigned);
    }

    /**
     * @return <code>true</code> if the Command is permitted to accept no arguments, otherwise <code>false</code>
     */
    protected boolean permitNoArgs() {
        return false;
    }

    /**
     * @param parser
     */
    @Override
    public void performAction(JCommander parser) throws CommandException {
        printUsage(parser, false);
    }

    /**
     * Print usage information, then exit.
     *
     * @param jc  the command line parser containing usage information
     * @param success whether this is due to a successful operation
     */
    protected void printUsage(JCommander jc, boolean success) {
        printUsage(jc, success ? 0 : 255);
    }

    /**
     * Print usage information, then exit.
     *
     * @param parser   the command line parser containing usage information
     * @param exitCode the exit code
     */
    protected void printUsage(JCommander parser, int exitCode) {
        printUsage(parser);
        System.exit(exitCode);
    }

    /**
     * @param commandName   the
     * @param parser the command line parser containing usage information
     * @return a child Command for the given args, or <code>null</code> if not found
     */
    protected Command getChildAction(String commandName, JCommander parser) {
        // find implementation
        final Class<? extends Command> commandClass = commandMap.get(commandName);
        if (null != commandClass) {
            try {
                final Command command = injector.getInstance(commandClass);
                command.setParent(this);
                command.setCommandName(commandName);
                return command;
            } catch (Exception e) {
                throw new CommandException(String.format("Error getting child command for args: %s", commandName), e);
            }
        }
        return null;
    }

    private JCommander getCommand(JCommander in) {
        JCommander jc = in;
        while (jc.getParsedCommand() != null) {
            jc = jc.getCommands().get(jc.getParsedCommand());
        }
        return jc;
    }

    private String getCommandChain(JCommander in) {
        String chain = in.getProgramName() + " ";
        JCommander jc = in;
        while (jc.getParsedCommand() != null) {
            chain += jc.getParsedCommand() + " ";
            jc = jc.getCommands().get(jc.getParsedCommand());
        }
        return chain;
    }

    private void printUsage(JCommander jc) {
        System.out.println(usage(jc, new StringBuilder()).toString());
    }

    private StringBuilder usage(JCommander parent, StringBuilder sb) {
        JCommander jc = getCommand(parent);
        StringBuilder intermediary = new StringBuilder("Usage: " + getCommandChain(parent));
        // Handle arguments
        List<ParameterDescription> parameters = jc.getParameters();
        parameters.sort((e1, e2) -> {
            int mandatory = -Boolean.compare(e1.getParameter().required(), e2.getParameter().required());
            return mandatory != 0 ? mandatory : e1.getLongestName().compareTo(e2.getLongestName());
        });

        // Build parameter list
        for (ParameterDescription param : parameters) {
            // Optional open braces
            if (!param.getParameter().required()) {
                intermediary.append("[");
            } else {
                intermediary.append("(");
            }

            intermediary.append(param.getNames());

            // Optional close braces
            if (!param.getParameter().required()) {
                intermediary.append("]");
            } else {
                intermediary.append(")");
            }

            intermediary.append(" ");
        }

        // Doing it this way in case we decide to have width limits.
        if (intermediary.length() > 0) {
            sb.append(intermediary);
        }

        // Handle sub-commands
        if (!jc.getCommands().isEmpty()) {
            sb.append("<command> [<args>]");
            sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
            sb.append("The following commands are available:");
            sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);

            // Each command
            jc.getCommands().forEach((key, value) -> {
                sb.append("   ");
                sb.append(key).append(": ");
                sb.append(jc.getCommandDescription(key));
                sb.append(LINE_SEPARATOR);
            });
        }

        // Handle arguments
        if (!jc.getParameters().isEmpty()) {
            sb.append(LINE_SEPARATOR);
            sb.append("The following arguments are available:");
            sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);

            parameters.forEach(param -> {
                // Foo: Description
                sb.append("   ");
                sb.append(param.getNames() + ": ");
                sb.append(param.getDescription());
                // If there is a default set and it's not a boolean
                if (param.getDefault() != null &&
                        !(param.getDefault() instanceof Boolean)) {
                    sb.append(" [default: ");
                    sb.append(param.getDefault());
                    sb.append("]");
                }
                sb.append(LINE_SEPARATOR);
            });
        }

        return sb;
    }


    public void setLogDebug(boolean logDebug) {
        this.logDebug = logDebug;
    }

    public boolean getLogDebug() {
        return logDebug;
    }
}
