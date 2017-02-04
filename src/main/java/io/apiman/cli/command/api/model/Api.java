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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Api {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String organizationName;

    /**
     * Note: use {@link #version} instead for declarative API configuration.
     */
    @JsonProperty
    private String initialVersion;

    @JsonProperty
    private String version;

    @JsonProperty
    private String status;

    public Api() {
    }

    public Api(String name, String description, String initialVersion) {
        this.name = name;
        this.description = description;
        this.initialVersion = initialVersion;
    }

    public String getName() {
        return name;
    }

    public void setInitialVersion(String initialVersion) {
        this.initialVersion = initialVersion;
    }

    public String getInitialVersion() {
        return initialVersion;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }
}
