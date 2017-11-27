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
import retrofit.converter.JacksonConverter;

import static io.apiman.cli.util.AuthUtil.HEADER_AUTHORIZATION;
import static io.apiman.cli.util.MappingUtil.JSON_MAPPER;

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
    protected A buildClient(Class<A> apiClass, String endpoint, String username, String password, boolean debugLogging) {
        final RestAdapter.Builder builder = new RestAdapter.Builder() //
                .setConverter(new JacksonConverter(JSON_MAPPER))
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
