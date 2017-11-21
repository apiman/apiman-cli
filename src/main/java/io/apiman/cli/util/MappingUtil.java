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

package io.apiman.cli.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiGateway;
import io.apiman.cli.command.api.model.EndpointProperties;
import io.apiman.cli.command.api.model.ServiceConfig;
import io.apiman.cli.command.declarative.model.DeclarativeApiConfig;
import io.apiman.cli.command.declarative.model.DeclarativeEndpointSecurity;
import io.apiman.cli.command.declarative.model.DeclarativeGateway;
import io.apiman.cli.managerapi.core.gateway.model.Gateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Shared POJO/JSON/YAML mapping utility methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public final class MappingUtil {
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

    private MappingUtil() {
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
     * Unmarshall the contents of given string into instance of klazz
     *
     * @param dataAsString the encoded data
     * @param klazz the type to unmarshall into
     * @param <T> the type to unmarshall into
     * @return the unmarshalled instance of klazz
     */
    public static <T> T readJsonValue(String dataAsString, Class<T> klazz) {
        try {
            return JSON_MAPPER.readValue(dataAsString, klazz);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading JSON from: %s", dataAsString), e);
        }
    }

    /**
     * Unmarshall the contents of given URL into an instance of klazz
     *
     * @param url the URL to read
     * @param klazz the class to marshall into
     * @return the unmarshalled representation
     */
    public static <T> T readJsonValue(URL url, Class<T> klazz) {
        try {
            return JSON_MAPPER.readValue(url, klazz);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading JSON from: %s", url), e);
        }
    }

    /**
     * Unmarshall the contents of given URL into a collection of type klazz.
     *
     * @param url the URL to read
     * @param collectionClazz the collection class to unmarshall into (e.g. List.class)
     * @param targetClazz the target class to unmarshall into
     * @return the unmarshalled representation
     */
    public static <C extends Collection<? super T>, T> C readJsonValue(URL url, Class<C> collectionClazz, Class<T> targetClazz) {
        try {
            return JSON_MAPPER.readValue(url, TypeFactory.defaultInstance().constructCollectionType(collectionClazz, targetClazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode:" + e.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Return an instance of {@code destinationClass} with a copy of identical fields to those found
     * in {@code source}.
     *
     * @param source           the source object
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
     * Configures a ModelMapper with some specific post-conversion steps.
     *
     * @return a configured ModelMapper
     */
    private static ModelMapper buildModelMapper() {
        final ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        // PostConverter for ApiConfig -> ServiceConfig
        mapper.createTypeMap(ApiConfig.class, ServiceConfig.class).setPostConverter(context -> {
            final ServiceConfig serviceConfig = context.getDestination();
            serviceConfig.setPublicService(context.getSource().isPublicApi());
            return serviceConfig;
        });

        // PostConverter for DeclarativeGateway -> Gateway
        mapper.createTypeMap(DeclarativeGateway.class, Gateway.class).setPostConverter(context -> {
            final Gateway gateway = context.getDestination();
            gateway.setConfiguration(safeWriteValueAsJson(context.getSource().getConfig()));
            return gateway;
        });

        // PostConverter for DeclarativeApiConfig -> ApiConfig
        mapper.createTypeMap(DeclarativeApiConfig.class, ApiConfig.class).setPostConverter(context -> {
            final DeclarativeApiConfig declarativeApiConfig = context.getSource();

            final ApiConfig apiConfig = context.getDestination();
            apiConfig.setPublicApi(declarativeApiConfig.isMakePublic());
            apiConfig.setGateways(Lists.newArrayList(new ApiGateway(declarativeApiConfig.getGateway())));

            return apiConfig;
        });

        // Converter for DeclarativeEndpointProperties -> EndpointProperties
        mapper.createTypeMap(DeclarativeEndpointSecurity.class, EndpointProperties.class).setConverter(context -> {
            final DeclarativeEndpointSecurity endpointSecurity = context.getSource();

            final EndpointProperties apiProperties = context.getDestination();
            apiProperties.setAuthorizationType(endpointSecurity.getAuthorizationType());
            apiProperties.setPassword(endpointSecurity.getPassword());
            apiProperties.setUsername(endpointSecurity.getUsername());
            apiProperties.setRequireSsl(endpointSecurity.getRequireSsl());

            return apiProperties;
        });

        return mapper;
    }
}
