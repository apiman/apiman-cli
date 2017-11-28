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

package io.apiman.cli.command;

import static io.apiman.cli.util.AuthUtil.DEFAULT_SERVER_PASSWORD;
import static io.apiman.cli.util.AuthUtil.DEFAULT_SERVER_USERNAME;
import static io.apiman.cli.util.LogUtil.LINE_SEPARATOR;

import io.apiman.cli.core.common.model.ManagementApiVersion;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.management.ManagementApiUtil;
import io.apiman.cli.util.LogUtil;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(AbstractCommand.class);
    private static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman";

    /**
     * Maps commands (e.g. 'org' or 'create') to their implementations.
     */
    private final Map<String, Class<? extends Command>> commandMap;

    @Option(name = "--debug", usage = "Log at DEBUG level")
    private boolean logDebug;

    @Option(name = "--help", aliases = {"-h"}, usage = "Display usage only", help = true)
    private boolean displayHelp;

    @Option(name = "--server", aliases = {"-s"}, usage = "Management API server address")
    protected String serverAddress = DEFAULT_SERVER_ADDRESS;

    @Option(name = "--serverUsername", aliases = {"-su"}, usage = "Management API server username")
    private String serverUsername = DEFAULT_SERVER_USERNAME;

    @Option(name = "--serverPassword", aliases = {"-sp"}, usage = "Management API server password")
    private String serverPassword = DEFAULT_SERVER_PASSWORD;

    /**
     * The parent Command (<code>null</code> if root).
     */
    private Command parent;

    /**
     * The name of this command.
     */
    private String commandName;
    private Injector injector;

    public AbstractCommand() {
        // get child commands
        commandMap = Maps.newHashMap();
        populateCommands(commandMap);
        injector = Guice.createInjector();
    }

    /**
     * Subclasses should populate the Map with their child Commands.
     *
     * @param commandMap the Map to populate
     */
    protected abstract void populateCommands(Map<String, Class<? extends Command>> commandMap);

    /**
     * @return human-readable short description for this command (e.g. 'Manage Plugins')
     */
    protected abstract String getCommandDescription();

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

    /**
     * @return the name of this command
     */
    @Override
    public String getCommandName() {
        return commandName;
    }

    /**
     * See {@link Command#run(List)}
     */
    @Override
    public void run(List<String> args) {
        final CmdLineParser parser = new CmdLineParser(this);

        if (!permitNoArgs() && 0 == args.size()) {
            printUsage(parser, false);
            return;
        }

        final Command child = getChildAction(args, parser);

        if (null == child) {
            try {
                parser.parseArgument(args);

                // update log config based on parsed arguments
                LogUtil.configureLogging(logDebug);

                if (displayHelp) {
                    printUsage(parser, true);
                } else {
                    performAction(parser);
                }

            } catch (CmdLineException e) {
                // handling of wrong arguments
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(e);
                } else {
                    LOGGER.error(e.getMessage());
                }

                printUsage(parser, false);

            } catch (ExitWithCodeException ec) {
                // print the message and exit with the given code
                LogUtil.OUTPUT.error(ec.getMessage());

                if (ec.isPrintUsage()) {
                    printUsage(parser, ec.getExitCode());
                } else {
                    System.exit(ec.getExitCode());
                }

            } catch (Exception e) {
                LOGGER.error("Error in " + getCommandDescription(), e);
                System.exit(1);
            }

        } else {
            // begin execution
            child.run(args.subList(1, args.size()));
        }
    }

    /**
     * @return <code>true</code> if the Command is permitted to accept no arguments, otherwise <code>false</code>
     */
    protected boolean permitNoArgs() {
        return false;
    }

    /**
     * See {@link Command#performAction(CmdLineParser)}
     */
    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        printUsage(parser, false);
    }

    /**
     * Print usage information, then exit.
     *
     * @param parser  the command line parser containing usage information
     * @param success whether this is due to a successful operation
     */
    private void printUsage(CmdLineParser parser, boolean success) {
        printUsage(parser, success ? 0 : 255);
    }

    /**
     * Print usage information, then exit.
     *
     * @param parser   the command line parser containing usage information
     * @param exitCode the exit code
     */
    private void printUsage(CmdLineParser parser, int exitCode) {
        System.out.println(getCommandDescription() + " usage:");

        // additional usage message
        System.out.println(getAdditionalUsage());

        parser.printUsage(System.out);
        System.exit(exitCode);
    }

    /**
     * Returns additional usage information; by default this is a list of the supported child commands.
     *
     * @return additional usage information
     */
    protected String getAdditionalUsage() {

        final StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR);

        final String parentCommand = getCommandChain();

        if (commandMap.isEmpty()) {
            sb.append(" ");
            sb.append(parentCommand);
            sb.append(" [args...]");
            sb.append(LINE_SEPARATOR);

        } else {
            for (String commandName : commandMap.keySet()) {
                sb.append(" ");
                sb.append(parentCommand);
                sb.append(" ");
                sb.append(commandName);
                sb.append(" [args...]");
                sb.append(LINE_SEPARATOR);
            }
        }

        return sb.toString();
    }

    /**
     * See {@link Command#getCommandChain()}
     */
    @Override
    public String getCommandChain() {
        return (null != parent ? parent.getCommandChain() + " " : "") + getCommandName();
    }

    /**
     * @param args   the arguments
     * @param parser the command line parser containing usage information
     * @return a child Command for the given args, or <code>null</code> if not found
     */
    protected Command getChildAction(List<String> args, CmdLineParser parser) {
        final String commandName = args.get(0);

        // find implementation
        final Class<? extends Command> commandClass = commandMap.get(commandName);
        if (null != commandClass) {
            try {
                final Command command = injector.getInstance(commandClass);
                command.setParent(this);
                command.setCommandName(commandName);

                return command;
            } catch (Exception e) {
                throw new CommandException(String.format("Error getting child command for args: %s", args), e);
            }
        }
        return null;
    }

    /**
     * @param clazz the Class for which to build a client
     * @param <T>   the API interface
     * @return an API client for the given Class
     */
    protected <T> T buildServerApiClient(Class<T> clazz) {
        return buildServerApiClient(clazz, ManagementApiVersion.UNSPECIFIED);
    }

    /**
     * @param clazz         the Class for which to build a client
     * @param serverVersion the server version
     * @param <T>           the API interface
     * @return an API client for the given Class
     */
    protected <T> T buildServerApiClient(Class<T> clazz, ManagementApiVersion serverVersion) {
        return ManagementApiUtil.buildServerApiClient(
                clazz,
                getManagementApiEndpoint(),
                getManagementApiUsername(),
                getManagementApiPassword(),
                logDebug,
                serverVersion);
    }

    protected String getManagementApiEndpoint() {
        // TODO consider reading from config file/environment
        return serverAddress;
    }

    private String getManagementApiUsername() {
        // TODO consider reading from config file/environment
        return serverUsername;
    }

    private String getManagementApiPassword() {
        // TODO consider reading from config file/environment
        return serverPassword;
    }

    public void setLogDebug(boolean logDebug) {
        this.logDebug = logDebug;
    }

    public boolean getLogDebug() {
        return logDebug;
    }
}
