package io.apiman.cli.core.api.model;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
    private boolean requireSsl;

    @JsonProperty("basic-auth.username")
    private String username;

    @JsonProperty("basic-auth.password")
    private String password;

    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

    public void setRequireSsl(boolean requireSsl) {
        this.requireSsl = requireSsl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                    map.put(annotation.value(), Objects.toString(field.get(this)));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}
