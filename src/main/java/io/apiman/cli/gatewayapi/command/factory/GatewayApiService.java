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

package io.apiman.cli.gatewayapi.command.factory;

import com.google.inject.Inject;
import io.apiman.cli.gatewayapi.GatewayApi;
import io.apiman.cli.gatewayapi.GatewayCommon;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.services.WaitService;
import io.apiman.cli.util.LogUtil;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class GatewayApiService implements WaitService, GatewayHelper {

    private GatewayCommon gatewayConfig;
    private GatewayApiFactory gatewayApiFactory;

    @Inject
    public void setGatewayApiFactory(GatewayApiFactory gatewayApiFactory) {
        this.gatewayApiFactory = gatewayApiFactory;
    }

    public void configureEndpoint(GatewayCommon gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

    /**
     * See {@link GatewayApiFactory#build(String, String, String, boolean)}
     * @return the a GatewayApi instance
     */
    public GatewayApi buildGatewayApiClient() {
        return gatewayApiFactory.build(gatewayConfig.getGatewayApiEndpoint(),
                gatewayConfig.getGatewayApiUsername(),
                gatewayConfig.getGatewayApiPassword(),
                LogUtil.isLogDebug());
    }

    @Override
    public void waitForServer(int waitTime) {
        waitForServer(buildGatewayApiClient(), waitTime);
    }
}
