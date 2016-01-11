package io.apiman.cli.core.plugin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plugin {
    @JsonProperty
    private Integer id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String groupId;

    @JsonProperty
    private String artifactId;

    @JsonProperty
    private String version;

    @JsonProperty
    private String classifier;

    public Plugin() {
    }

    public Plugin(String groupId, String artifactId, String classifier, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.classifier = classifier;
        this.version = version;
    }
}
