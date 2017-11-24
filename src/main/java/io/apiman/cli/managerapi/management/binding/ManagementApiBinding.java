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

import com.google.inject.BindingAnnotation;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specialises an injection binding between an interface and implementation class. The {@link #value()}
 * represents the API interface that the implementation provides.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Retention(RUNTIME)
@Target({ElementType.TYPE})
@BindingAnnotation
public @interface ManagementApiBinding {
    Class<?> value();

    ManagementApiVersion serverVersion() default ManagementApiVersion.UNSPECIFIED;
}
