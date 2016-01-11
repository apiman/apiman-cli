package io.apiman.cli.action;

import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.function.Supplier;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete
 */
public abstract class AbstractAction implements Action {
    private static final Logger LOGGER = LogManager.getLogger(AbstractAction.class);
    private static final String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman";

    @Option(name = "--debug", usage = "Log at DEBUG level")
    private boolean logDebug;

    @Option(name = "--help", aliases = {"-h"}, usage = "Display usage only", help = true)
    private boolean displayHelp;

    @Option(name = "--server", aliases = {"-s"}, usage = "Management API server address")
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

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

    private void printUsage(CmdLineParser parser, boolean success) {
        System.out.println(getActionName() + " usage:");
        parser.printUsage(System.out);
        System.exit(success ? 0 : 255);
    }

    protected abstract Action getChildAction(List<String> args, CmdLineParser parser);

    protected String getManagementApiEndpoint() {
        return serverAddress;
    }

    protected <T> T getApiClient(Class<T> clazz) {
        final RestAdapter.Builder builder = new RestAdapter.Builder() //
                .setConverter(new JacksonConverter(JsonUtil.MAPPER))
                .setEndpoint(getManagementApiEndpoint())
                .setRequestInterceptor(request -> request.addHeader("Authorization", "Basic " + BaseEncoding.base64().encode("admin:admin123!".getBytes())));

        if (LOGGER.isDebugEnabled()) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(clazz);
    }

    protected void invokeAndCheckResponse(Supplier<Response> request) throws ActionException {
        invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
    }

    protected void invokeAndCheckResponse(int expectedStatus, Supplier<Response> request) throws ActionException {
        final Response response;
        try {
            // invoke the request
            response = request.get();

            // check response code is successful
            if (response.getStatus() != expectedStatus) {
                httpError(expectedStatus, response);
            }

        } catch (RetrofitError e) {
            httpError(expectedStatus, e.getResponse());
        }
    }

    private void httpError(int expectedStatus, Response response) throws ActionException {
        // obtain response body
        String body = null;
        try (InputStream errStream = response.getBody().in()) {
            body = CharStreams.toString(new InputStreamReader(errStream));
        } catch (IOException ignored) {
        }

        throw new ActionException("HTTP " + response.getStatus() + " " + response.getReason() + " but expected " + expectedStatus + ":\n" + body);
    }

    protected abstract String getActionName();
}
