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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Functional convenience methods.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class Functions<T> {
    private Optional<T> optional;

    private Functions(Optional<T> optional) {
        this.optional = optional;
    }

    public static <T> Functions<T> of(Optional<T> optional) {
        return new Functions<>(optional);
    }

    public Functions<T> ifPresent(Consumer<T> c) {
        optional.ifPresent(c);
        return this;
    }

    public Functions<T> ifNotPresent(Runnable r) {
        if (!optional.isPresent()) {
            r.run();
        }
        return this;
    }

    public static <T> Predicate<T> not(Predicate<T> p) {
        return t -> !p.test(t);
    }
}
