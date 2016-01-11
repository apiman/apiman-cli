package io.apiman.cli.action.gateway;

import org.kohsuke.args4j.Option;

/**
 * @author Pete
 */
public class GatewayCreateAction extends AbstractGatewayCreateAction {

    @Option(name = "--name", aliases = {"-n"}, usage = "Name", required = true)
    private String name;

    @Override
    protected String getGatewayName() {
        return name;
    }
}
