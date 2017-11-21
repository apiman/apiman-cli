/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.apiman.cli.command.declarative.model.DeclarativeOrg;
import io.apiman.cli.command.declarative.model.DeclarativeSystem;
import io.apiman.cli.command.declarative.model.SharedItems;

/**
 * Represents an API environment declaration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BaseDeclaration {
    @JsonProperty
    private DeclarativeSystem system;

    @JsonProperty
    private SharedItems shared;

    @JsonProperty
    private DeclarativeOrg org;

    public DeclarativeSystem getSystem() {
        return system;
    }

    public DeclarativeOrg getOrg() {
        return org;
    }

    public void setSystem(DeclarativeSystem system) {
        this.system = system;
    }

    public void setOrg(DeclarativeOrg org) {
        this.org = org;
    }

    public SharedItems getShared() {
        return shared;
    }
}
