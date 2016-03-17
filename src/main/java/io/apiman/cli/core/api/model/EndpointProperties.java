package io.apiman.cli.core.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointProperties {
    @JsonProperty("authorization.type")
    private String authorizationType;

    @JsonProperty("basic-auth.requireSSL")
    private Boolean requireSsl;

    @JsonProperty("basic-auth.username")
    private String username;

    @JsonProperty("basic-auth.password")
    private String password;

    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

    public void setRequireSsl(Boolean requireSsl) {
        this.requireSsl = requireSsl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
