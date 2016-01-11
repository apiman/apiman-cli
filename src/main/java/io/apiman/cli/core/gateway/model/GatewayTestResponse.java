package io.apiman.cli.core.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayTestResponse {
    @JsonProperty
    private boolean success;

    @JsonProperty
    private String detail;

    public boolean isSuccess() {
        return success;
    }

    public String getDetail() {
        return detail;
    }
}
