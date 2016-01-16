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

import io.apiman.cli.core.api.ApiApi;
import io.apiman.cli.core.api.ApiMixin;
import io.apiman.cli.core.api.ServiceApi;
import io.apiman.cli.core.api.model.Api;
import io.apiman.cli.exception.ActionException;
import io.apiman.cli.util.MappingUtil;
import io.apiman.cli.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import java.text.MessageFormat;
import java.util.List;

/**
 * List APIs.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiListAction extends AbstractApiAction implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiListAction.class);

   @Override
    protected String getActionName() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Listing {}", this::getModelName);

        List<Api> apis;
        switch (serverVersion) {
            case v119:
                // legacy apiman 1.1.9 support
                apis = buildApiClient(ServiceApi.class).list(orgName);
                break;

            default:
                apis = buildApiClient(ApiApi.class).list(orgName);
                break;
        }

        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(apis));
    }
}
