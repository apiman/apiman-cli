package io.apiman.cli.gatewayapi;

import com.beust.jcommander.Parameter;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayCommon {

    private String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman-gateway-api";
    private String DEFAULT_GATEWAY_USER = "apimanager";
    private String DEFAULT_GATEWAY_PASSWORD = "apiman123!";

    @Parameter(names = { "--server", "-s"}, description = "Gateway API server address")
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

    @Parameter(names = { "--serverUsername", "-su"}, description = "Gateway API server username")
    private String serverUsername = DEFAULT_GATEWAY_USER;

    @Parameter(names = { "--serverPassword", "-sp"}, description = "Gateway API server password")
    private String serverPassword = DEFAULT_GATEWAY_PASSWORD;

    public String getGatewayApiEndpoint() {
        return serverAddress;
    }

    public String getGatewayApiUsername() {
        return serverUsername;
    }

    public String getGatewayApiPassword() {
        return serverPassword;
    }
}
