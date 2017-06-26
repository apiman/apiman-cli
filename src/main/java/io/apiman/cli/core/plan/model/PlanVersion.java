package io.apiman.cli.core.plan.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models an Plan version.
 *
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanVersion {
    @JsonProperty
    private String version;

    /**
     * Never clone a previous version when creating a new version.
     */
    @JsonProperty
    final private boolean clone = false;

    public PlanVersion(String version) {
        this.version = version;
    }
}
