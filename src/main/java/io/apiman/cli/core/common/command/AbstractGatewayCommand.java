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

package io.apiman.cli.core.common.command;

import com.beust.jcommander.ParametersDelegate;
import com.google.inject.Inject;
import io.apiman.cli.command.core.AbstractFinalCommand;
import io.apiman.cli.command.GatewayCommon;
import io.apiman.cli.core.api.GatewayApi;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.managerapi.management.factory.GatewayApiFactory;
import io.apiman.gateway.engine.beans.SystemStatus;

import java.text.MessageFormat;

/**
 * Common model CRUD functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractGatewayCommand extends AbstractFinalCommand implements GatewayHelper {
    @ParametersDelegate
    private GatewayCommon gatewayCommonConfig = new GatewayCommon();
    private GatewayApiFactory apiFactory;

    public GatewayCommon getGatewayConfig() {
        return gatewayCommonConfig;
    }

    @Inject
    public void setGatewayApiFactory(GatewayApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    protected GatewayApiFactory getApiFactory() {
        return apiFactory;
    }

    protected void versionCheck(String availableSince) {
        GatewayApi gatewayApi = buildGatewayApiClient(apiFactory, getGatewayConfig());
        statusCheck(gatewayApi, getGatewayConfig().getGatewayApiEndpoint());
        SystemStatus systemStatus = gatewayApi.getSystemStatus();

        VersionHolder local = new VersionHolder(availableSince);
        VersionHolder remote = new VersionHolder(systemStatus.getVersion());

        if (remote.compareTo(local) < 0) {
            String message = MessageFormat.format("Remote API version {0} does not support this command. " +
                            "It is available in version {1} or later.", systemStatus.getVersion(), availableSince);
            throw new CommandException(message);
        }
    }

    private static final class VersionHolder {
        private final String version;
        private String[] splitVersion;
        private boolean isSnapshot;

        VersionHolder(String version) {
            this.version = version;
            parse(version);
        }

        private void parse(String version) {
            if (version.endsWith("-SNAPSHOT")) {
                isSnapshot = true;
                LOGGER.debug("A snapshot version is being used, behaviour may be inconsistent.");
            }
            // Remove any character classifiers, should be irrelevant for this. E.g. 3.1.3.Final => 3.1.3
            String strippedVersion = version.replaceAll("[^\\.\\d]", "");
            splitVersion = strippedVersion.split("\\.");
        }

        int compareTo(VersionHolder otherVersion) {
            String[] otherSplitVersion = otherVersion.splitVersion;
            int len = Math.min(splitVersion.length, otherSplitVersion.length);

            for (int i = 0; i < len; i++) {
                if (!splitVersion[i].equalsIgnoreCase(otherSplitVersion[i])) {
                    return compare(splitVersion[i], otherSplitVersion[i]);
                }
            }
            return Integer.signum(splitVersion.length - otherSplitVersion.length);
        }

        private int compare(String a, String b) {
            return Integer.signum(Integer.valueOf(a).compareTo(Integer.valueOf(b)));
        }
    }

}
