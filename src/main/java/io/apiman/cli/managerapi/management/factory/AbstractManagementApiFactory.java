/*
 * Copyright 2016 Pete Cornish
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

package io.apiman.cli.managerapi.management.factory;

import io.apiman.cli.util.AuthUtil;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import static io.apiman.cli.util.AuthUtil.HEADER_AUTHORIZATION;
import static io.apiman.cli.util.MappingUtil.JSON_MAPPER;

import java.lang.reflect.Type;

/**
 * Builds a Management API client proxy for a given API interface.
 *
 * @param <T> the requested API interface
 * @param <A> the actual API interface
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractManagementApiFactory<T, A> implements ManagementApiFactory<T> {
    /**
     * @param apiClass     the Class for which to build a client
     * @param username     the management API username
     * @param password     the management API password
     * @param debugLogging whether debug logging should be enabled
     * @return an API client for the given Class
     */
    protected A buildClient(Class<A> apiClass, String endpoint, String username, String password, boolean debugLogging, PostConverter postConverter) {
    	final JacksonConverter jacksonConverter = new JacksonConverter(JSON_MAPPER);
    	final Converter converter;
    	if (postConverter == null) {
    		converter = jacksonConverter;
    	} else {
    		converter = new Converter() {

				@Override
				public Object fromBody(TypedInput body, Type type) throws ConversionException {
					final Object o = jacksonConverter.fromBody(body, type);
					postConverter.postConvert(o);
					return o;
				}

				@Override
				public TypedOutput toBody(Object object) {
					return jacksonConverter.toBody(object);
				}
    			
    		};
    	}
    	
        final RestAdapter.Builder builder = new RestAdapter.Builder() //
                .setConverter(converter)
                .setEndpoint(endpoint)
                .setRequestInterceptor(request -> {
                    request.addHeader(HEADER_AUTHORIZATION, AuthUtil.buildAuthString(username, password));
                });

        if (debugLogging) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return builder.build().create(apiClass);
    }
}
