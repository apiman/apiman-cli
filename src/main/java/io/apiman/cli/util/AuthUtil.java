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

import com.google.common.io.BaseEncoding;

/**
 * Utility class for authenticating access to the management API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public final class AuthUtil {
    /**
     * Authorization HTTP header.
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Basic Authorization scheme prefix.
     */
    private static final String AUTH_BASIC = "Basic ";

    /**
     * Management API username.
     */
    public static final String DEFAULT_SERVER_USERNAME = "admin";

    /**
     * Management API password.
     */
    public static final String DEFAULT_SERVER_PASSWORD = "admin123!";

    /**
     * Encoded credentials for Basic auth.
     */
    public static final String BASIC_AUTH_VALUE = buildAuthString(DEFAULT_SERVER_USERNAME, DEFAULT_SERVER_PASSWORD);

    private AuthUtil() {
    }

    /**
     * @param username the username
     * @param password the password
     * @return an basic authentication string for the given credentials
     */
    public static String buildAuthString(String username, String password) {
        return AUTH_BASIC + BaseEncoding.base64().encode((username + ":" + password).getBytes());
    }
}
