package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Declaration {
    @JsonProperty
    private DeclarativeSystem system;

    @JsonProperty
    private DeclarativeOrg org;

    public DeclarativeSystem getSystem() {
        return system;
    }

    public DeclarativeOrg getOrg() {
        return org;
    }

    public void setSystem(DeclarativeSystem system) {
        this.system = system;
    }

    public void setOrg(DeclarativeOrg org) {
        this.org = org;
    }
}
