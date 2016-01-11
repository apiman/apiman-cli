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

package io.apiman.cli.core.api.action;

import io.apiman.cli.core.common.action.AbstractModelAction;
import io.apiman.cli.core.api.ApiApi;
import io.apiman.cli.core.api.ApiMixin;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.core.common.model.ServerVersion;
import org.kohsuke.args4j.Option;

/**
 * Common API functionality.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class AbstractApiAction extends AbstractModelAction<Api, ApiApi> implements ApiMixin {
    @Option(name = "--orgName", aliases = {"-o"}, usage = "Organisation name", required = true)
    protected String orgName;

    @Option(name = "--serverVersion", aliases = {"-sv"}, usage = "Management API server version")
    protected ServerVersion serverVersion = ServerVersion.v119;
}
