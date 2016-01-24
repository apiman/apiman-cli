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

import io.apiman.cli.core.common.util.ServerActionUtil;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.core.api.ApiMixin;
import io.apiman.cli.core.common.ActionApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.text.MessageFormat;

/**
 * Publish an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiPublishAction extends AbstractApiAction implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiPublishAction.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "API name", required = true)
    private String name;

    @Option(name = "--version", aliases = {"-v"}, usage = "API version", required = true)
    private String version;

    @Override
    protected String getActionName() {
        return MessageFormat.format("Publish {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Publishing {}", this::getModelName);
        ServerActionUtil.publishApi(orgName, name, version, serverVersion, buildServerApiClient(ActionApi.class));
    }

}
