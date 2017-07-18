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

package io.apiman.cli.gatewayapi;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.jayway.restassured.RestAssured;
import io.apiman.cli.Cli;
import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.WaitForHttp;
import io.apiman.cli.util.MappingUtil;
import io.apiman.gateway.engine.beans.Api;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
public class GatewayBaseTest {
    /**
     * Basic auth header.
     */
    protected static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Gateway API username.
     */
    protected static final String APIMAN_USERNAME = "apimanager";

    /**
     * Gateway API password.
     */
    protected static final String APIMAN_PASSWORD = "apiman123!";

    /**
     * Encoded credentials for Basic auth.
     */
    protected static final String BASIC_AUTH_VALUE = "Basic " + BaseEncoding.base64().encode(
            (APIMAN_USERNAME + ":" + APIMAN_PASSWORD).getBytes());

    /**
     * Wait for apiman to be available.
     * Returns a 200 on 'http://docker.local:8080/apiman-gateway-api/system/status' when ready.
     */
    @ClassRule
    public static WaitForHttp apiman = new WaitForHttp(getApimanHost(), getApimanPort(), "/apiman-gateway-api/system/status")
            .withStatusCode(HttpURLConnection.HTTP_OK)
            .withBasicCredentials(APIMAN_USERNAME, APIMAN_PASSWORD);

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
        return apiman.getAddress() + "/apiman-gateway-api";
    }

    protected void applyDeclaration(String resourcePath) throws IOException, URISyntaxException {
        Cli.main("gateway",
            "apply",
            "--debug",
            "-f", getResourceAsPath(resourcePath).toAbsolutePath().toString());
    }

    protected Api getApi(String org, String apiId, String version) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream currentOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Cli.main("gateway",
                "api",
                "list",
                "--org", org,
                "--api", apiId,
                "--version", version,
                "--server", getApimanUrl(),
                "--serverUsername", APIMAN_USERNAME,
                "--serverPassword", APIMAN_PASSWORD);

        System.setOut(currentOut);
        //System.out.println(new String(outputStream.toByteArray()));
        return MappingUtil.readJsonValue(new String(outputStream.toByteArray()), Api.class);
    }

    protected <T> T expectJson(String resource, Class<T> klazz) throws URISyntaxException, MalformedURLException { // TODO rename to unmarshall JSON?
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
