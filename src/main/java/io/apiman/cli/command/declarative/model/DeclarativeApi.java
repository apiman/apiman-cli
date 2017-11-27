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

package io.apiman.cli.command.declarative.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.apiman.cli.command.api.model.Api;

import java.util.List;

/**
 * Declarative API representation.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
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
