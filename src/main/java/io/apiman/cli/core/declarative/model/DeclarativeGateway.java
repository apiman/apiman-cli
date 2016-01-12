package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.apiman.cli.core.gateway.model.Gateway;
import io.apiman.cli.core.gateway.model.GatewayConfig;

/**
 * @author pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeclarativeGateway extends Gateway {
    @JsonProperty
    private GatewayConfig config;

    public GatewayConfig getConfig() {
        return config;
    }

    public void setConfig(GatewayConfig config) {
        this.config = config;
    }
}
