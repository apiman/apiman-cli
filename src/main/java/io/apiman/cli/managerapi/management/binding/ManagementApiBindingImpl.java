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

package io.apiman.cli.managerapi.management.binding;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.apiman.cli.managerapi.core.common.model.ManagementApiVersion;

import java.lang.annotation.Annotation;

/**
 * Specialises an injection binding between an interface and implementation class. The {@link #value()}
 * represents the API interface that the implementation provides.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiBindingImpl implements ManagementApiBinding {
    private final Class<?> value;
    private final ManagementApiVersion serverVersion;

    public ManagementApiBindingImpl(Class<?> apiClass) {
        this(apiClass, ManagementApiVersion.UNSPECIFIED);
    }

    public ManagementApiBindingImpl(Class<?> apiClass, ManagementApiVersion serverVersion) {
        this.value = apiClass;
        this.serverVersion = serverVersion;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ManagementApiBinding.class;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", value)
                .add("serverVersion", serverVersion)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagementApiBindingImpl that = (ManagementApiBindingImpl) o;
        return Objects.equal(value, that.value) &&
                serverVersion == that.serverVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, serverVersion);
    }

    @Override
    public Class<?> value() {
        return this.value;
    }

    public ManagementApiVersion serverVersion() {
        return serverVersion;
    }
}
