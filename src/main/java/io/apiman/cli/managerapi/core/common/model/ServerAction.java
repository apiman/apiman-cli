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

package io.apiman.cli.managerapi.core.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Models a server action.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerAction {
    @JsonProperty
    private String type;

    @JsonProperty
    private String entityId;

    @JsonProperty
    private String organizationId;

    @JsonProperty
    private String entityVersion;

    public ServerAction() {
    }

    public ServerAction(String type, String organizationId, String entityId, String entityVersion) {
        this.type = type;
        this.organizationId = organizationId;
        this.entityId = entityId;
        this.entityVersion = entityVersion;
    }
}
