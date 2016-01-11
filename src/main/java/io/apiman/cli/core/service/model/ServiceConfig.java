package io.apiman.cli.core.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceConfig {
    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String endpointType;

    @JsonProperty
    private boolean publicService;

    @JsonProperty
    private List<ServiceGateway> gateways;

    public ServiceConfig() {
    }

    public ServiceConfig(String endpoint, String endpointType, boolean publicService, List<ServiceGateway> gateways) {
        this.endpoint = endpoint;
        this.endpointType = endpointType;
        this.publicService = publicService;
        this.gateways = gateways;
    }
}
