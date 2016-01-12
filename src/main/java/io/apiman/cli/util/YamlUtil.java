package io.apiman.cli.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author pete
 */
public class YamlUtil {
    public static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper(new YAMLFactory());
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
