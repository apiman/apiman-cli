/*
 * Copyright 2017 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.gatewayapi.command.generate;

import io.apiman.cli.common.BaseTest;
import io.apiman.cli.gatewayapi.command.generate.GenerateHeadless.HeadlessConfigBean;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.PolicyResolver;
import io.apiman.gateway.engine.beans.Api;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.modelmapper.internal.util.Assert.isTrue;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GenerateHeadlessTest extends BaseTest {
    private static final boolean LOG_DEBUG = true;

    private GenerateHeadless command;

    @Mock
    private GenerateHeadless.JsonWriter mJsonWriter = mock(GenerateHeadless.JsonWriter.class);

    @Before
    public void setUp() {
        command = new GenerateHeadless();
        command.setPolicyResolver(new PolicyResolver());
        // Configure logging level
        command.setLogDebug(LOG_DEBUG);
        LogUtil.configureLogging(LOG_DEBUG);
    }

    /**
     * Write a real file to ensure the format is correct.
     */
    @Test
    public void testGenerateConfig_withRealFileOutput()  throws Exception {
        String outputPath = "/tmp/plugin-and-builtin-policies_headless.json";

        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/plugin-and-builtin-policies.yml"));
        command.outputFiles.add(Paths.get(outputPath));
        // Run
        command.applyDeclaration();
        // Verify file exists
        isTrue(Files.exists(Paths.get(outputPath)));
        // Read the output file back in, and compare with expected result
        String expectedFileOutput = new String(Files.readAllBytes(Paths.get(outputPath)));
        Assert.assertEquals(getResourceAsString("/gateway/generateHeadless/expected-headless_plugin-and-builtin-policies.json"), expectedFileOutput);
    }

    @Test
    public void testGenerateConfig_writeToDirectory() throws Exception {
        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/plugin-and-builtin-policies.yml"));
        command.outputFiles.add(Paths.get("/tmp"));
        // Run
        command.applyDeclaration();
        // Verify file exists
        isTrue(Files.exists(Paths.get("/tmp/test-gw.json"))); // Note generated name
        // Read the output file back in, and compare with expected result
        String expectedFileOutput = new String(Files.readAllBytes(Paths.get("/tmp/test-gw.json")));
        Assert.assertEquals(getResourceAsString("/gateway/generateHeadless/expected-headless_plugin-and-builtin-policies.json"), expectedFileOutput);
    }

    @Test
    public void testGenerateConfig_withExplicitOutputFilename() throws Exception {
        command.setJsonWriter(mJsonWriter);

        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/plugin-and-builtin-policies.yml"));
        command.outputFiles.add(Paths.get("/tmp/someOutputFile.json")); // Doesn't matter, we're stubbing this.
        // Run
        command.applyDeclaration();
        // Verify
        Api expectedApi = expectJson("/gateway/plugin-and-builtin-policies-expectation.json", Api.class);
        // We expect the above API to be written
        HeadlessConfigBean expectedWrite = new HeadlessConfigBean(Arrays.asList(expectedApi), Collections.emptyList());
        verify(mJsonWriter).write(eq(Paths.get("/tmp/someOutputFile.json")), refEq(expectedWrite));
    }

    @Test
    public void testGenerateConfig_withTransformedName() throws Exception {
        command.setJsonWriter(mJsonWriter);

        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/transform-name.yml"));
        command.outputFiles.add(Paths.get("/tmp"));
        // Run
        command.applyDeclaration();
        // Verify
        HeadlessConfigBean expected = expectJson("/gateway/generateHeadless/transform-name.json", HeadlessConfigBean.class);
        // Space-type characters and forward slashes should be substituted with -
        verify(mJsonWriter).write(eq(Paths.get("/tmp/gateway-with-various-spaces-in-original-᠎-name-and-forward-slash--.json")), refEq(expected));
    }

    @Test
    public void testGenerateConfig_withMultipleGatewayConfigs_ExplicitName() throws Exception {
        command.setJsonWriter(mJsonWriter);

        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/plugin-and-builtin-policies_multiple-gateways.yml"));
        command.outputFiles.add(Paths.get("/tmp/someOutputFile.json")); // Doesn't matter, we're stubbing this.
        command.outputFiles.add(Paths.get("/tmp/someOutputFile2.json")); // Doesn't matter, we're stubbing this.
        // Run
        command.applyDeclaration();
        // Verify it matches our reference configs.
        HeadlessConfigBean expectedConfig1 = expectJson("/gateway/generateHeadless/expected-multiple-gateways.json", HeadlessConfigBean.class);
        HeadlessConfigBean expectedConfig2 = expectJson("/gateway/generateHeadless/expected-multiple-gateways-2.json", HeadlessConfigBean.class);

        verify(mJsonWriter).write(eq(Paths.get("/tmp/someOutputFile.json")), refEq(expectedConfig1));
        verify(mJsonWriter).write(eq(Paths.get("/tmp/someOutputFile2.json")), refEq(expectedConfig2));
    }

    @Test
    public void testGenerateConfig_withMultipleGatewayConfigs_GeneratedName() throws Exception {
        command.setJsonWriter(mJsonWriter);

        command.setDeclarationFile(getResourceAsPath("/gateway/generateHeadless/plugin-and-builtin-policies_multiple-gateways.yml"));
        command.outputFiles.add(Paths.get("/tmp")); // Directory.
        // Run
        command.applyDeclaration();
        // Verify it matches our reference configs.
        HeadlessConfigBean expectedConfig1 = expectJson("/gateway/generateHeadless/expected-multiple-gateways.json", HeadlessConfigBean.class);
        HeadlessConfigBean expectedConfig2 = expectJson("/gateway/generateHeadless/expected-multiple-gateways-2.json", HeadlessConfigBean.class);

        verify(mJsonWriter).write(eq(Paths.get("/tmp/test-gw-2.json")), refEq(expectedConfig1)); // Notice generated filenames
        verify(mJsonWriter).write(eq(Paths.get("/tmp/test-gw.json")), refEq(expectedConfig2));
    }
}
