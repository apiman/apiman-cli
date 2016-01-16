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

package io.apiman.cli.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shared JSON/YAML mapping utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class MappingUtil {
    private static final Logger LOGGER = LogManager.getLogger(MappingUtil.class);

    public static final ObjectMapper JSON_MAPPER;
    public static final ObjectMapper YAML_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        YAML_MAPPER = new ObjectMapper(new YAMLFactory());
        YAML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * @param obj the Object to write as a JSON String
     * @return the {@code obj} as JSON, or {@code null} if an error occurs
     */
    public static String safeWriteValueAsJson(Object obj) {
        try {
            return MappingUtil.JSON_MAPPER.writeValueAsString(obj);

        } catch (NullPointerException | JsonProcessingException e) {
            LOGGER.trace(String.format("Error writing value as JSON string: %s", obj), e);
            return null;
        }
    }
}
