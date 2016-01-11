package io.apiman.cli.core.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApimanAction {
    @JsonProperty
    private String type;

    @JsonProperty
    private String entityId;

    @JsonProperty
    private String organizationId;

    @JsonProperty
    private String entityVersion;

    public ApimanAction() {
    }

    public ApimanAction(String type, String organizationId, String entityId, String entityVersion) {
        this.type = type;
        this.organizationId = organizationId;
        this.entityId = entityId;
        this.entityVersion = entityVersion;
    }
}
