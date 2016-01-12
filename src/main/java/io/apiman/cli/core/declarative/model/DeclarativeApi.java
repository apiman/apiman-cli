package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.apiman.cli.core.api.model.Api;

import java.util.List;

/**
 * @author pete
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeclarativeApi extends Api {
    @JsonProperty
    private boolean published;

    @JsonProperty
    private DeclarativeApiConfig config;

    @JsonProperty
    private List<DeclarativePolicy> policies;

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public DeclarativeApiConfig getConfig() {
        return config;
    }

    public void setConfig(DeclarativeApiConfig config) {
        this.config = config;
    }

    public List<DeclarativePolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<DeclarativePolicy> policies) {
        this.policies = policies;
    }
}
