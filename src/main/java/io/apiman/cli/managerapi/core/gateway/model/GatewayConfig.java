/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.managerapi.core.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models gateway configuration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayConfig {
    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public GatewayConfig() {
    }

    public GatewayConfig(String endpoint, String username, String password) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
