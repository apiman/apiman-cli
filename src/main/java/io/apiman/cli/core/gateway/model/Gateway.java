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

package io.apiman.cli.core.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a gateway.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gateway {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private GatewayType type;

    @JsonProperty
    private String configuration;

    public Gateway() {
    }

    public Gateway(String name, String description, GatewayType type, String configuration) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.configuration = configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }
}