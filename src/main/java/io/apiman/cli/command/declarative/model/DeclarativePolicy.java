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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Declarative policy representation.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="$id")
public class DeclarativePolicy {
    @JsonProperty("$id")
    private String id;

    @JsonProperty
    private String name;

    private String plugin;

    @JsonProperty("config")
    @JsonSerialize(using = JsonNodeToStringSerializer.class, as = String.class)
    @JsonDeserialize(using = StringToJsonNodeDeserializer.class)
    private JsonNode config;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getConfig() {
        return config;
    }

    public void setConfig(JsonNode config) {
        this.config = config;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public boolean isPlugin() {
        return !(plugin == null || plugin.isEmpty());
    }

	public void setId(String id) {
		this.id = id;
	}


    public static final class JsonNodeToStringSerializer extends JsonSerializer<JsonNode> {

        @Override
        public void serialize(JsonNode tmpNode,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeObject(tmpNode.toString());
        }
    }

    public static final class StringToJsonNodeDeserializer extends JsonDeserializer<JsonNode> {
        public StringToJsonNodeDeserializer() {}

        @Override
        public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return p.readValueAsTree();
        }
    }
}
