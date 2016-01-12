package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.apiman.cli.core.org.model.Org;

import java.util.List;

/**
 * @author pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeclarativeOrg extends Org {
    @JsonProperty
    private List<DeclarativeApi> apis;

    public List<DeclarativeApi> getApis() {
        return apis;
    }

    public void setApis(List<DeclarativeApi> apis) {
        this.apis = apis;
    }
}
