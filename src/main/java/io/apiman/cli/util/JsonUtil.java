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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shared JSON utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class JsonUtil {
    public static final ObjectMapper MAPPER;
    private static final Logger LOGGER = LogManager.getLogger(JsonUtil.class);

    static {
        MAPPER = new ObjectMapper();
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * @param obj the Object to write as JSON
     * @return the {@code obj} as JSON, or {@code null} if an error occurs
     */
    public static String safeWriteValueAsString(Object obj) {
        try {
            return JsonUtil.MAPPER.writeValueAsString(obj);

        } catch (NullPointerException | JsonProcessingException e) {
            LOGGER.trace(String.format("Error writing value as string: %s", obj), e);
            return null;
        }
    }
}
