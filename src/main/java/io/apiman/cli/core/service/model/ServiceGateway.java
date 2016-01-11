package io.apiman.cli.core.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceGateway {
    @JsonProperty
    private String gatewayId;

    public ServiceGateway() {
    }

    public ServiceGateway(String gatewayId) {
        this.gatewayId = gatewayId;
    }
}
