package io.apiman.cli.gatewayapi.command.factory;

import io.apiman.cli.gatewayapi.GatewayApi;
import io.apiman.cli.managerapi.management.factory.AbstractManagementApiFactory;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayApiFactory extends AbstractManagementApiFactory<GatewayApi, GatewayApi> {

    @Override
    public GatewayApi build(String endpoint, String username, String password, boolean debugLogging) {
        return buildClient(GatewayApi.class, endpoint, username, password, debugLogging);
    }
}
