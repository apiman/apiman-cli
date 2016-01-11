package io.apiman.cli.core.gateway.action;

import io.apiman.cli.api.action.common.ModelShowAction;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.gateway.GatewayMixin;
import io.apiman.cli.api.exception.ActionException;
import io.apiman.cli.core.gateway.model.Gateway;
import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class GatewayShowAction extends ModelShowAction<Gateway, GatewayApi>
        implements GatewayMixin {

    @Option(name = "--name", aliases = {"-n"}, usage = "Name")
    private String name;

    @Override
    protected String getModelId() throws ActionException {
        return name;
    }
}
