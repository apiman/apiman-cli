package io.apiman.cli.action.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.model.GatewayTestResponse;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.MessageFormat;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete
 */
public class GatewayTestAction extends AbstractGatewayCreateAction {
    private static final Logger LOGGER = LogManager.getLogger(GatewayTestAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = false)
    private String name;

    @Override
    protected String getActionName() {
        return MessageFormat.format("Test {0}", getModelName());
    }

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Testing {}", this::getModelName);

        GatewayTestResponse response;
        try {
            final GatewayApi apiClient = getApiClient(GatewayApi.class);
            response = apiClient.test(buildModelInstance());

            OUTPUT.info("Test {}", () -> response.isSuccess() ? "successful" : "failed");

            LOGGER.debug("Test result: {}", () -> {
                try {
                    return JsonUtil.MAPPER.writeValueAsString(response);
                } catch (JsonProcessingException ignored) {
                    return null;
                }
            });

        } catch (Exception e) {
            throw new ActionException(e);
        }

        if (!response.isSuccess()) {
            throw new ExitWithCodeException(1, MessageFormat.format("Test failed: {0}", response.getDetail()));
        }
    }

    @Override
    protected String getGatewayName() {
        return null;
    }
}
