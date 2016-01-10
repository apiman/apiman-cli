package io.apiman.cli.action.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.action.AbstractFinalAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Gateway;
import io.apiman.cli.model.GatewayConfig;
import io.apiman.cli.model.GatewayType;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.net.HttpURLConnection;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayCreateAction extends AbstractFinalAction {
    private static final Logger LOGGER = LogManager.getLogger(GatewayCreateAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Option(name = "--endpoint", aliases = {"-e"}, usage = "Endpoint")
    private String endpoint;

    @Option(name = "--username", aliases = {"-u"}, usage = "Username")
    private String username;

    @Option(name = "--password", aliases = {"-p"}, usage = "Password")
    private String password;

    @Option(name = "--type", aliases = {"-t"}, usage = "type")
    private GatewayType type;

    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        final String config;
        try {
            config = JsonUtil.MAPPER.writeValueAsString(new GatewayConfig(endpoint,
                    username,
                    password));

        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }

        invokeAndCheckResponse(() -> getApiClient(GatewayApi.class)
                .create(new Gateway(name,
                        description,
                        type,
                        config)), HttpURLConnection.HTTP_OK);
    }

    @Override
    protected String getActionName() {
        return "Create gateway";
    }
}
