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

package io.apiman.cli.managerapi.declarative.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import io.apiman.cli.command.declarative.command.AbstractApplyCommand;
import io.apiman.cli.command.declarative.model.BaseDeclaration;
import io.apiman.cli.managerapi.ManagerCommon;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.service.DeclarativeService;
import io.apiman.cli.managerapi.service.ManagementApiService;
import io.apiman.cli.managerapi.service.PluginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;

@Parameters(commandDescription = "Apply Apiman Manager declaration")
public class ManagerApplyCommand extends AbstractApplyCommand {
    private static final Logger LOGGER = LogManager.getLogger(ManagerApplyCommand.class);

    @Parameter(names = {"--serverVersion", "-sv"}, description = "Management API server version")
    private ManagementApiVersion serverVersion = ManagementApiVersion.DEFAULT_VERSION;

    @ParametersDelegate
    private final ManagerCommon managerCommon;
    private final DeclarativeService declarativeService;
    private final PluginService pluginService;

    @Inject
    public ManagerApplyCommand(ManagementApiService managementApiService,
                        DeclarativeService declarativeService,
                               PluginService pluginService) {
        super(managementApiService);
        this.declarativeService = declarativeService;
        this.pluginService = pluginService;
        this.managerCommon = new ManagerCommon(managementApiService);
    }

    /**
     * Apply the given Declaration.
     *
     * @param declaration the Declaration to apply.
     */
    @Override
    protected void applyDeclaration(BaseDeclaration declaration) {
        LOGGER.debug("Applying declaration");

        // add gateways
        ofNullable(declaration.getSystem().getGateways()).ifPresent(declarativeService::applyGateways);

        // add plugins
        ofNullable(declaration.getSystem().getPlugins()).ifPresent(pluginService::addPlugins);

        // add org and APIs
        ofNullable(declaration.getOrg()).ifPresent(org -> {
            declarativeService.applyOrg(org);

            ofNullable(org.getApis()).ifPresent(apis ->
                    declarativeService.applyApis(serverVersion, apis, org.getName()));
        });

        LOGGER.info("Applied declaration");
    }

    public void setServerAddress(String serverAddress) {
        managerCommon.setServerAddress(serverAddress);
    }

    public void setServerVersion(ManagementApiVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
