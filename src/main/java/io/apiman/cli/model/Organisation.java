package io.apiman.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    public Organisation() {
    }

    public Organisation(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
