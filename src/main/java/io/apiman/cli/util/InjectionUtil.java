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

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.apiman.cli.managerapi.management.ManagementApiFactoryModule;
import io.apiman.cli.managerapi.service.ServiceModule;

/**
 * Manages the injection context.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public final class InjectionUtil {
    private static Injector injector;

    private InjectionUtil() {
    }

    public synchronized static Injector getInjector() {
        if (null == injector) {
            injector = Guice.createInjector(
                    new ManagementApiFactoryModule(),
                    new ServiceModule()
            );
        }
        return injector;
    }
}
