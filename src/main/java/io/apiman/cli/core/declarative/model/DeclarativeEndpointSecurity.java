package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeclarativeEndpointSecurity {
    @JsonProperty
    private String authorizationType;

    @JsonProperty
    private boolean requireSsl;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public String getAuthorizationType() {
        return authorizationType;
    }

    public boolean getRequireSsl() {
        return requireSsl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
