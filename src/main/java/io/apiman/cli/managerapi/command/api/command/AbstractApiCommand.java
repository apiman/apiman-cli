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

package io.apiman.cli.managerapi.command.api.command;

import com.beust.jcommander.Parameter;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.managerapi.command.api.ApiMixin;
import io.apiman.cli.managerapi.command.api.Version12xServerApi;
import io.apiman.cli.managerapi.command.common.command.AbstractManagerModelCommand;
import io.apiman.cli.managerapi.command.common.model.ManagementApiVersion;
import io.apiman.cli.managerapi.service.ManagementApiService;

/**
 * Common API functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractApiCommand extends AbstractManagerModelCommand<Api, Version12xServerApi>
        implements ApiMixin {
    @Parameter(names = { "--orgName", "-o"}, description = "Organisation name", required = true)
    protected String orgName;

    @Parameter(names = { "--serverVersion", "-sv"}, description = "Management API server version")
    protected ManagementApiVersion serverVersion = ManagementApiVersion.DEFAULT_VERSION;

    AbstractApiCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }
}
