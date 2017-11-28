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

package io.apiman.cli.command;

import io.apiman.cli.common.BaseTest;
import io.apiman.cli.common.IntegrationTest;
import io.apiman.cli.core.api.GatewayApi;
import io.apiman.cli.core.declarative.command.GatewayApplyCommand;
import io.apiman.cli.management.factory.GatewayApiFactory;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.PolicyResolver;
import io.apiman.gateway.engine.beans.Api;
import io.apiman.gateway.engine.beans.SystemStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(IntegrationTest.class)
public class GatewayDeclarativeTest extends BaseTest {
    private static final boolean LOG_DEBUG = true;

    private GatewayApplyCommand command;
    @Mock
    GatewayApiFactory mGatewayApiFactory = mock(GatewayApiFactory.class);
    @Mock
    GatewayApi mGatewayApi = mock(GatewayApi.class);

    @Before
    public void setUp() {
        command = new GatewayApplyCommand();
        command.setGatewayApiFactory(mGatewayApiFactory);
        command.setPolicyResolver(new PolicyResolver());
        // Configure logging level
        command.setLogDebug(LOG_DEBUG);
        LogUtil.configureLogging(LOG_DEBUG);
        // Stub Gateway API Factory
        when(mGatewayApiFactory.build("http://localhost:8080/apiman-gateway-api", "apimanager", "apiman123!", true))
                .thenReturn(mGatewayApi);
        // Bake in OK status
        SystemStatus okStatus = new SystemStatus();
        okStatus.setUp(true);
        when(mGatewayApi.getSystemStatus()).thenReturn(okStatus);
    }

    @Test
    public void testApplyDeclaration_PluginAndBuiltInPolicies() throws Exception {
        command.setDeclarationFile(getResourceAsPath("/gateway/plugin-and-builtin-policies.yml"));
        Api expected = expectJson("/gateway/plugin-and-builtin-policies-expectation.json", Api.class);
        // Run
        command.applyDeclaration();
        // Verify
        verify(mGatewayApi).publishApi(Mockito.argThat(api -> EqualsBuilder.reflectionEquals(api, expected)));
    }

    /**
     * As the gateway doesn't use descriptive fields, just ignore them. They can still be present for informational
     * purposes.
     * <p>
     * For example: 'description: "Multi-version example API V1'
     *
     * @throws Exception any exception
     */
    @Test
    public void testApplyDeclaration_IgnoreUnusedFields() throws Exception {
        command.setDeclarationFile(getResourceAsPath("/multiple-versions.yml"));
        Api expected1 = expectJson("/gateway/multiple-versions-expectation-1.json", Api.class);
        Api expected2 = expectJson("/gateway/multiple-versions-expectation-2.json", Api.class);
        // Run
        command.applyDeclaration();
        // Verify
        verify(mGatewayApi).publishApi(Mockito.argThat(api -> EqualsBuilder.reflectionEquals(api, expected1)));
        verify(mGatewayApi).publishApi(Mockito.argThat(api -> EqualsBuilder.reflectionEquals(api, expected2)));
    }

    /**
     * Two policy versions, with the latter containing security properties that must be converted into endpoint
     * properties such as 'basic-auth.username', etc.
     *
     * @throws Exception any exception
     */
    @Test
    public void testApplyDeclaration_SharedPolicies() throws Exception {
        command.setDeclarationFile(getResourceAsPath("/shared-policies.yml"));
        Api expected1 = expectJson("/gateway/shared-policies-expectation-1.json", Api.class);
        Api expected2 = expectJson("/gateway/shared-policies-expectation-2.json", Api.class);
        // Run
        command.applyDeclaration();
        // Verify
        verify(mGatewayApi).publishApi(Mockito.argThat(api -> EqualsBuilder.reflectionEquals(api, expected1)));
        verify(mGatewayApi).publishApi(Mockito.argThat(api -> EqualsBuilder.reflectionEquals(api, expected2)));
    }
}
