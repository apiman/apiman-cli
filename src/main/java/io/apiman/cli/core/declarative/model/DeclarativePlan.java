package io.apiman.cli.core.declarative.model;
/*
 * Copyright 2017 Jean-Charles Quantin
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


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.apiman.cli.core.plan.model.Plan;

import java.util.List;

/**
 * Declarative Plan representation.
 *
 * @author Jean-Charles Quantin {@literal <jeancharles.quantin@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeclarativePlan extends Plan {

    @JsonProperty
    private List<DeclarativePolicy> policies;

    public List<DeclarativePolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<DeclarativePolicy> policies) {
        this.policies = policies;
    }

}