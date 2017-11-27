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

package io.apiman.cli.managerapi.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Dependency injection configuration for services.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ManagementApiService.class).to(ManagementApiServiceImpl.class).in(Singleton.class);
        bind(ApiService.class).to(ApiServiceImpl.class).in(Singleton.class);
        bind(PluginService.class).to(PluginServiceImpl.class).in(Singleton.class);
        bind(PolicyService.class).to(PolicyServiceImpl.class).in(Singleton.class);
        bind(DeclarativeService.class).to(DeclarativeServiceImpl.class).in(Singleton.class);
    }
}
