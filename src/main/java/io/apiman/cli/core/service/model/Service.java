package io.apiman.cli.core.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {
    @JsonProperty
    private String name;

    @JsonProperty
    private String initialVersion;

    @JsonProperty
    private String version;

    public Service() {
    }

    public Service(String name, String initialVersion) {
        this.name = name;
        this.initialVersion = initialVersion;
    }
}
