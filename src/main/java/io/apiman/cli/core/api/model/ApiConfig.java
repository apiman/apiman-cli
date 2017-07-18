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

package io.apiman.cli.core.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Support for apiman 1.2.x.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiConfig {
    @JsonProperty
    private String endpoint;

    @JsonProperty
    private String endpointType;

    @JsonProperty
    private EndpointProperties endpointProperties;

    @JsonProperty("publicAPI")
    private boolean publicApi;

    @JsonProperty
    private List<ApiGateway> gateways;

    public ApiConfig() {
    }

    public ApiConfig(String endpoint, String endpointType, boolean publicApi, List<ApiGateway> gateways) {
        this.endpoint = endpoint;
        this.endpointType = endpointType;
        this.publicApi = publicApi;
        this.gateways = gateways;
    }

    public List<ApiGateway> getGateways() {
        return gateways;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public EndpointProperties getEndpointProperties() {
        return endpointProperties;
    }

    public void setGateways(ArrayList<ApiGateway> gateways) {
        this.gateways = gateways;
    }

    public void setPublicApi(boolean publicApi) {
        this.publicApi = publicApi;
    }

    public boolean isPublicApi() {
        return publicApi;
    }

    public void setEndpointProperties(EndpointProperties endpointProperties) {
        this.endpointProperties = endpointProperties;
    }
}
