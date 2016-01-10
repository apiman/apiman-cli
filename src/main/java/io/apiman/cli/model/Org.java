package io.apiman.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Org {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    public Org() {
    }

    public Org(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
