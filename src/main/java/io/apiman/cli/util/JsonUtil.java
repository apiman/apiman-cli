package io.apiman.cli.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

/**
 * @author Pete
 */
public class JsonUtil {
    public static final ObjectMapper MAPPER;
    private static final Logger LOGGER = LogManager.getLogger(JsonUtil.class);

    static {
        MAPPER = new ObjectMapper();
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static String safeWriteValueAsString(Object obj) {
        try {
            return JsonUtil.MAPPER.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            LOGGER.trace(String.format("Error writing value as string: %s", obj), e);
            return null;
        }
    }
}
