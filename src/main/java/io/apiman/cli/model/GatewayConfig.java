package io.apiman.cli.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
public class GatewayConfig {
    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public GatewayConfig() {
    }

    public GatewayConfig(String endpoint, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }
}
