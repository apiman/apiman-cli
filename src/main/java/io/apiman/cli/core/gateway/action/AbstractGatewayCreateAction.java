package io.apiman.cli.core.gateway.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.api.action.common.ModelCreateAction;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.GatewayMixin;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.gateway.model.GatewayConfig;
import io.apiman.cli.core.gateway.model.GatewayType;
import io.apiman.cli.util.JsonUtil;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public abstract class AbstractGatewayCreateAction extends ModelCreateAction<Gateway, GatewayApi>
        implements GatewayMixin {

    @Option(name = "--description", aliases = {"-d"}, usage = "Description")
    private String description;

    @Option(name = "--endpoint", aliases = {"-e"}, usage = "Endpoint", required = true)
    private String endpoint;

    @Option(name = "--username", aliases = {"-u"}, usage = "Username")
    private String username;

    @Option(name = "--password", aliases = {"-p"}, usage = "Password")
    private String password;

    @Option(name = "--type", aliases = {"-t"}, usage = "type")
    private GatewayType type = GatewayType.REST;

    @Override
    protected Gateway buildModelInstance() throws ActionException {
        final String config;
        try {
            config = JsonUtil.MAPPER.writeValueAsString(
                    new GatewayConfig(endpoint,
                            username,
                            password));

        } catch (JsonProcessingException e) {
            throw new ActionException(e);
        }

        return new Gateway(getGatewayName(),
                description,
                type,
                config);
    }

    protected abstract String getGatewayName();
}
