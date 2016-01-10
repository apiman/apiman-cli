package io.apiman.cli.action.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apiman.cli.action.common.ModelCreateAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Gateway;
import io.apiman.cli.model.GatewayConfig;
import io.apiman.cli.model.GatewayType;
import io.apiman.cli.util.JsonUtil;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class GatewayCreateAction extends ModelCreateAction<Gateway, GatewayApi>
        implements GatewayMixin {

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

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

        return new Gateway(name,
                description,
                type,
                config);
    }
}
