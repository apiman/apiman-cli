/*
 * Copyright 2016 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.common;

import com.google.common.base.Strings;
import com.jayway.restassured.RestAssured;
import com.google.common.io.BaseEncoding;
import io.apiman.cli.Cli;
import io.apiman.cli.util.AuthUtil;
import io.apiman.cli.util.MappingUtil;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.apiman.cli.util.Functions.not;
import static java.util.Optional.ofNullable;

/**
 * This base class waits for an instance of apiman.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class BaseTest {
    /**
     * Basic auth header.
     */
    protected static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Management API username.
     */
    protected static final String APIMAN_USERNAME = "admin";

    /**
     * Management API password.
     */
    protected static final String APIMAN_PASSWORD = "admin123!";

    /**
     * Encoded credentials for Basic auth.
     */
    protected static final String BASIC_AUTH_VALUE = "Basic " + BaseEncoding.base64().encode(
            (APIMAN_USERNAME + ":" + APIMAN_PASSWORD).getBytes());

    /**
     * Wait for apiman to be available.
     * Returns a 200 on 'http://docker.local:8080/apiman/system/status' when ready.
     */
    @ClassRule
    public static WaitForHttp apiman = new WaitForHttp(getApimanHost(), getApimanPort(), "/apiman/system/status")
            .withStatusCode(HttpURLConnection.HTTP_OK)
            .withBasicCredentials(AuthUtil.DEFAULT_SERVER_USERNAME, AuthUtil.DEFAULT_SERVER_PASSWORD);

    private static String getApimanHost() {
        return ofNullable(System.getProperty("apiman.host"))
                .filter(not(Strings::isNullOrEmpty))
                .orElse("localhost");
    }

    private static int getApimanPort() {
        return ofNullable(System.getProperty("apiman.port"))
                .filter(not(Strings::isNullOrEmpty))
                .map(Integer::parseInt)
                .orElse(8080);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        RestAssured.baseURI = getApimanUrl();
    }

    protected static String getApimanUrl() {
        return apiman.getAddress() + "/apiman";
    }

    /**
     * Create an org with the given name.
     *
     * @param orgName the org name
     */
    protected void createOrg(String orgName) {
        Cli.main("manager",
                "org", "create",
                "--debug",
                "--server", getApimanUrl(),
                "--serverUsername", AuthUtil.DEFAULT_SERVER_USERNAME,
                "--serverPassword", AuthUtil.DEFAULT_SERVER_PASSWORD,
                "--name", orgName,
                "--description", "example");
    }

    protected <T> T expectJson(String resource, Class<T> klazz) throws URISyntaxException, MalformedURLException {
        return MappingUtil.readJsonValue(getResourceAsURL(resource), klazz);
    }

    protected URL getResourceAsURL(String resource) throws URISyntaxException, MalformedURLException {
        return BaseTest.class.getResource(resource).toURI().toURL();
    }

    protected Path getResourceAsPath(String resource) throws URISyntaxException, MalformedURLException {
        return Paths.get(getResourceAsURL(resource).toURI());
    }

    protected String getResourceAsString(String resource) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(getResourceAsURL(resource).toURI())));
    }
}
