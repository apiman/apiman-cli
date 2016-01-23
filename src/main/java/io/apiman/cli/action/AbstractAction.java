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

import com.google.common.collect.Maps;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.util.ApiUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.List;
import java.util.Map;

import static io.apiman.cli.util.LogUtil.LINE_SEPARATOR;
import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractAction implements Action {
    private static final Logger LOGGER = LogManager.getLogger(AbstractAction.class);
    private static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman";
    private static final String DEFAULT_SERVER_USERNAME = "admin";
    private static final String DEFAULT_SERVER_PASSWORD = "admin123!";

    /**
     * Maps action commands (e.g. 'org' or 'create') to their implementations.
     */
    private final Map<String, Class<? extends Action>> actionMap;

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
     * The parent Action (<code>null</code> if root).
     */
    private Action parent;
    private String command;

    public AbstractAction() {
        // get child actions
        actionMap = Maps.newHashMap();
        populateActions(actionMap);
    }

    /**
     * Subclasses should populate the Map with their child Actions.
     *
     * @param actionMap the Map to populate
     */
    protected abstract void populateActions(Map<String, Class<? extends Action>> actionMap);

    /**
     * @return human-readable name for this action (e.g. 'Manage Plugins')
     */
    protected abstract String getActionName();

    @Override
    public void setParent(Action parent) {
        this.parent = parent;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {
        return command;
    }

    /**
     * See {@link Action#run(List)}
     */
    @Override
    public void run(List<String> args) {
        final CmdLineParser parser = new CmdLineParser(this);

        if (!permitNoArgs() && 0 == args.size()) {
            printUsage(parser, false);
            return;
        }

        final Action child = getChildAction(args, parser);

        if (null == child) {
            try {
                parser.parseArgument(args);

                if (logDebug) {
                    final LoggerContext context = (LoggerContext) LogManager.getContext(false);
                    context.getConfiguration().getRootLogger().setLevel(Level.DEBUG);
                    context.updateLoggers();
                }

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
                OUTPUT.error(ec.getMessage());

                if (ec.isPrintUsage()) {
                    printUsage(parser, ec.getExitCode());
                } else {
                    System.exit(ec.getExitCode());
                }

            } catch (Exception e) {
                LOGGER.error("Error in " + getActionName(), e);
            }

        } else {
            // begin execution
            child.run(args.subList(1, args.size()));
        }
    }

    /**
     * @return <code>true</code> if the Action is permitted to accept no arguments, otherwise <code>false</code>
     */
    protected boolean permitNoArgs() {
        return false;
    }

    /**
     * See {@link Action#performAction(CmdLineParser)}
     */
    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
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
        System.out.println(getActionName() + " usage:");

        // additional usage message
        System.out.println(getAdditionalUsage());

        parser.printUsage(System.out);
        System.exit(exitCode);
    }

    /**
     * Returns additional usage information; by default this is a list of the supported child actions.
     *
     * @return additional usage information
     */
    protected String getAdditionalUsage() {

        final StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR);

        final String parentCommand = getCommandChain();

        if (actionMap.isEmpty()) {
            sb.append(" ");
            sb.append(parentCommand);
            sb.append(" [args...]");
            sb.append(LINE_SEPARATOR);

        } else {
            for (String actionCommand : actionMap.keySet()) {
                sb.append(" ");
                sb.append(parentCommand);
                sb.append(" ");
                sb.append(actionCommand);
                sb.append(" [args...]");
                sb.append(LINE_SEPARATOR);
            }
        }

        return sb.toString();
    }

    /**
     * See {@link Action#getCommandChain()}
     */
    @Override
    public String getCommandChain() {
        return (null != parent ? parent.getCommandChain() + " " : "") + getCommand();
    }

    /**
     * @param args   the arguments
     * @param parser the command line parser containing usage information
     * @return a child Action for the given args, or <code>null</code> if not found
     */
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        final String command = args.get(0);

        // find implementation
        final Class<? extends Action> actionClass = actionMap.get(command);
        if (null != actionClass) {
            try {
                final Action action = actionClass.newInstance();
                action.setParent(this);
                action.setCommand(command);

                return action;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ActionException(String.format("Error getting child action for args: %s", args), e);
            }
        }
        return null;
    }

    /**
     * @param clazz the Class for which to build a client
     * @param <T>   the API interface
     * @return an API client for the given Class
     */
    protected <T> T buildApiClient(Class<T> clazz) {
        return ApiUtil.buildApiClient(
                clazz,
                getManagementApiEndpoint(),
                getManagementApiUsername(),
                getManagementApiPassword(),
                logDebug);
    }

    protected String getManagementApiEndpoint() {
        // TODO read from config/environment
        return serverAddress;
    }

    private String getManagementApiUsername() {
        // TODO read from config/environment
        return serverUsername;
    }

    private String getManagementApiPassword() {
        // TODO read from config/environment
        return serverPassword;
    }

    public void setLogDebug(boolean logDebug) {
        this.logDebug = logDebug;
    }
}
