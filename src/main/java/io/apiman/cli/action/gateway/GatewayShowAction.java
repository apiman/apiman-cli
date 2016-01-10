package io.apiman.cli.action.gateway;

import io.apiman.cli.action.common.ModelShowAction;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.model.Gateway;
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
