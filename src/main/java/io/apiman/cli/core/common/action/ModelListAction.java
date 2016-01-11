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

package io.apiman.cli.core.common.action;

import io.apiman.cli.exception.ActionException;
import io.apiman.cli.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import static io.apiman.cli.util.LogUtil.OUTPUT;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class ModelListAction<M, A> extends AbstractModelAction<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelListAction.class);

    @Override
    protected String getActionName() {
        return MessageFormat.format("List {0}s", getModelName());
    }

    @Override
    protected boolean permitNoArgs() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Listing {}", this::getModelName);

        try {
            final A apiClient = buildApiClient(getApiClass());
            final Method listMethod = apiClient.getClass().getMethod("list");
            processList((List<M>) listMethod.invoke(apiClient));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ActionException(e);
        }
    }

    protected void processList(List<M> model) {
        OUTPUT.info(JsonUtil.safeWriteValueAsString(model));
    }
}
