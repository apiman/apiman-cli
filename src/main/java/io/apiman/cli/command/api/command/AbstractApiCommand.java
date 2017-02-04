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

package io.apiman.cli.command.api.command;

import io.apiman.cli.command.common.command.AbstractModelCommand;
import io.apiman.cli.command.api.Version12xServerApi;
import io.apiman.cli.command.api.ApiMixin;
import io.apiman.cli.command.api.model.Api;
import io.apiman.cli.command.common.model.ManagementApiVersion;
import io.apiman.cli.service.ManagementApiService;
import org.kohsuke.args4j.Option;

/**
 * Common API functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractApiCommand extends AbstractModelCommand<Api, Version12xServerApi> implements ApiMixin {
    @Option(name = "--orgName", aliases = {"-o"}, usage = "Organisation name", required = true)
    protected String orgName;

    @Option(name = "--serverVersion", aliases = {"-sv"}, usage = "Management API server version")
    protected ManagementApiVersion serverVersion = ManagementApiVersion.DEFAULT_VERSION;

    AbstractApiCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }
}
