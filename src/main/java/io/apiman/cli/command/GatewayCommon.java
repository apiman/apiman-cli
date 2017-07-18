package io.apiman.cli.command;

import com.beust.jcommander.Parameter;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayCommon {

    String DEFAULT_SERVER_ADDRESS = "http://localhost:8080/apiman-gateway-api";
    String DEFAULT_GATEWAY_USER = "apimanager";
    String DEFAULT_GATEWAY_PASSWORD = "apiman123!";

    @Parameter(names = { "--server", "-s"}, description = "Gateway API server address")
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

    @Parameter(names = { "--serverUsername", "-su"}, description = "Gateway API server username")
    private String serverUsername = DEFAULT_GATEWAY_USER;

    @Parameter(names = { "--serverPassword", "-sp"}, description = "Gateway API server password")
    private String serverPassword = DEFAULT_GATEWAY_PASSWORD;

    public String getGatewayApiEndpoint() {
        // TODO consider reading from config file/environment. Also think about alternative auth methods.
        return serverAddress;
    }

    public String getGatewayApiUsername() {
        // TODO consider reading from config file/environment. Also think about alternative auth methods.
        return serverUsername;
    }

    public String getGatewayApiPassword() {
        // TODO consider reading from config file/environment. Also think about alternative auth methods.
        return serverPassword;
    }
}
