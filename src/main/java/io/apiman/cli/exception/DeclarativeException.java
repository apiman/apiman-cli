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

package io.apiman.cli.exception;

/**
 * Thrown due to a failure to apply a declaration.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class DeclarativeException extends RuntimeException {
    public DeclarativeException() {
        super();
    }

    public DeclarativeException(Throwable cause) {
        super(cause);
    }

    public DeclarativeException(String message) {
        super(message);
    }

    public DeclarativeException(String message, Throwable cause) {
        super(message, cause);
    }
}
