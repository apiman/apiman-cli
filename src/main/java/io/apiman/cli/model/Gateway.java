package io.apiman.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gateway {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private GatewayType type;

    @JsonProperty
    private String configuration;

    public Gateway() {
    }

    public Gateway(String name, String description, GatewayType type, String configuration) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.configuration = configuration;
    }
}
