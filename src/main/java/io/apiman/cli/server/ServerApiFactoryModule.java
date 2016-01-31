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

package io.apiman.cli.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.apiman.cli.core.api.factory.Version11XServerApiFactoryImpl;
import io.apiman.cli.core.api.factory.Version12XServerApiFactoryImpl;
import io.apiman.cli.core.api.VersionAgnosticApi;
import io.apiman.cli.core.common.ActionApi;
import io.apiman.cli.core.common.model.ServerVersion;
import io.apiman.cli.core.gateway.GatewayApi;
import io.apiman.cli.core.org.OrgApi;
import io.apiman.cli.core.plugin.PluginApi;
import io.apiman.cli.server.binding.ServerApiBindings;
import io.apiman.cli.server.factory.ServerApiFactory;
import io.apiman.cli.server.factory.SimpleServerApiFactoryImpl;

/**
 * Bindings for server API factories.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ServerApiFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(GatewayApi.class))
                .toInstance(new SimpleServerApiFactoryImpl<>(GatewayApi.class));

        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(OrgApi.class))
                .toInstance(new SimpleServerApiFactoryImpl<>(OrgApi.class));

        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(PluginApi.class))
                .toInstance(new SimpleServerApiFactoryImpl<>(PluginApi.class));

        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(ActionApi.class))
                .toInstance(new SimpleServerApiFactoryImpl<>(ActionApi.class));

        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(VersionAgnosticApi.class, ServerVersion.v11x))
                .to(Version11XServerApiFactoryImpl.class).in(Singleton.class);

        bind(ServerApiFactory.class)
                .annotatedWith(ServerApiBindings.boundTo(VersionAgnosticApi.class, ServerVersion.v12x))
                .to(Version12XServerApiFactoryImpl.class).in(Singleton.class);
    }
}
