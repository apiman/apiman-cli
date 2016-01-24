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
import com.google.common.collect.Lists;
import io.apiman.cli.core.api.model.ApiConfig;
import io.apiman.cli.core.api.model.ApiGateway;
import io.apiman.cli.core.api.model.ServiceConfig;
import io.apiman.cli.core.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.core.declarative.model.DeclarativeGateway;
import io.apiman.cli.core.gateway.model.Gateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.spi.MappingContext;

/**
 * Shared POJO/JSON/YAML mapping utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class MappingUtil {
    private static final Logger LOGGER = LogManager.getLogger(MappingUtil.class);

    /**
     * JSON -> POJO
     */
    public static final ObjectMapper JSON_MAPPER;

    /**
     * YAML -> POJO
     */
    public static final ObjectMapper YAML_MAPPER;

    /**
     * POJO -> POJO
     */
    public static final ModelMapper MODEL_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        YAML_MAPPER = new ObjectMapper(new YAMLFactory());
        YAML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        MODEL_MAPPER = buildModelMapper();
    }

    /**
     * @param obj the Object to write as a JSON String
     * @return the {@code obj} as JSON, or {@code null} if an error occurs
     */
    public static String safeWriteValueAsJson(Object obj) {
        try {
            return JSON_MAPPER.writeValueAsString(obj);

        } catch (NullPointerException | JsonProcessingException e) {
            LOGGER.trace(String.format("Error writing value as JSON string: %s", obj), e);
            return null;
        }
    }

    /**
     * Return an instance of {@code destinationClass} with a copy of identical fields to those found
     * in {@code source}.
     *
     * @param source         the source object
     * @param destinationClass the return type Class definition
     * @param <D>              the return type
     * @param <S>              the source type
     * @return an instance of {@code destinationClass} containing the copied fields
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        try {
            /*
             * Explicitly instantiate the destination to avoid ModelMapper returning the source object
             * in cases where the destination type is assignable from the source type.
             */
            final D destination = destinationClass.newInstance();

            MODEL_MAPPER.map(source, destination);

            return destination;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures a ModelMapper with some specific converters.
     *
     * @return a configured ModelMapper
     */
    private static ModelMapper buildModelMapper() {
        final ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        // ApiConfig -> ServiceConfig
        mapper.addConverter((MappingContext<ApiConfig, ServiceConfig> context) -> {
            final ServiceConfig serviceConfig = context.getMappingEngine().map(context);
            serviceConfig.setPublicService(context.getSource().isPublicApi());
            return serviceConfig;
        });

        // DeclarativeGateway -> Gateway
        mapper.addConverter((MappingContext<DeclarativeGateway, Gateway> context) -> {
            final Gateway gateway = context.getMappingEngine().map(context);
            gateway.setConfiguration(safeWriteValueAsJson(context.getSource().getConfig()));
            return gateway;
        });

        // DeclarativeApiConfig -> ApiConfig
        mapper.addConverter((MappingContext<DeclarativeApiConfig, ApiConfig> context) -> {
            final DeclarativeApiConfig declarativeApiConfig = context.getSource();

            final ApiConfig apiConfig = context.getMappingEngine().map(context);
            apiConfig.setPublicApi(declarativeApiConfig.isMakePublic());
            apiConfig.setGateways(Lists.newArrayList(new ApiGateway(declarativeApiConfig.getGateway())));

            return apiConfig;
        });

        return mapper;
    }
}
