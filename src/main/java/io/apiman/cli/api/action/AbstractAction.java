package io.apiman.cli.api.action;

import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.api.exception.ExitWithCodeException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

import java.util.List;
import java.util.Map;

import static io.apiman.cli.util.JsonUtil.MAPPER;
import static io.apiman.cli.util.LogUtil.LINE_SEPARATOR;
import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete
 */
public abstract class AbstractAction implements Action {
    private static final Logger LOGGER = LogManager.getLogger(AbstractAction.class);
    private static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman";
    private static final String DEFAULT_SERVER_USERNAME = "apiman";
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
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

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
                // print the message to output and exit with the given code
                OUTPUT.error(ec.getMessage());
                System.exit(ec.getExitCode());
                return;

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
     * Default implementation will print usage and exit with an error code.
     * Subclasses should implement their custom behaviour here.
     *
     * @param parser
     */
    protected void performAction(CmdLineParser parser) throws ActionException {
        printUsage(parser, false);
    }

    /**
     * Print usage information.
     *
     * @param parser  the command line parser containing usage information
     * @param success whether this is due to a successful operation
     */
    private void printUsage(CmdLineParser parser, boolean success) {
        System.out.println(getActionName() + " usage:");

        // additional usage message
        System.out.println(getAdditionalUsage());

        parser.printUsage(System.out);
        System.exit(success ? 0 : 255);
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
        final RestAdapter.Builder builder = new RestAdapter.Builder() //
                .setConverter(new JacksonConverter(MAPPER))
                .setEndpoint(getManagementApiEndpoint())
                .setRequestInterceptor(request -> {
                    final String credentials = String.format("%s:%s", getManagementApiUsername(), getManagementApiPassword());
                    request.addHeader("Authorization", "Basic " + BaseEncoding.base64().encode(credentials.getBytes()));
                });

        if (LOGGER.isDebugEnabled()) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(clazz);
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
}
