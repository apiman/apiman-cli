/*
 * Copyright 2017 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.command.api.model;

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
