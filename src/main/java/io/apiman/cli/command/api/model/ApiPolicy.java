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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Models a policy to be added to an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiPolicy {
    /**
     * Note: this is a request property.
     */
    @JsonProperty
    private String definitionId;

    /**
     * This is a response property.
     */
    @JsonProperty
    private String policyDefinitionId;

    @JsonProperty
    private String configuration;

    /**
     * This is a response property.
     */
    @JsonProperty
    private Long id;

    public ApiPolicy() {
    }

    public ApiPolicy(String configuration) {
        this.configuration = configuration;
    }

    public String getPolicyDefinitionId() {
        return policyDefinitionId;
    }

    public Long getId() {
        return id;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }
}
