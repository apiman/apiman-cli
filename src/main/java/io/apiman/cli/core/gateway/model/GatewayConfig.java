package io.apiman.cli.core.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
