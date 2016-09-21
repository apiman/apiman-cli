package io.apiman.cli.core.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models an API version.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiVersion {
    @JsonProperty
    private String version;

    /**
     * Never clone a previous version when creating a new version.
     */
    @JsonProperty
    final private boolean clone = false;

    public ApiVersion(String version) {
        this.version = version;
    }
}
