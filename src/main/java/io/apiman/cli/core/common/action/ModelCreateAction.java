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
import io.apiman.cli.util.ApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import retrofit.client.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class ModelCreateAction<M, A> extends AbstractModelAction<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelCreateAction.class);

    @Override
    protected String getActionName() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws ActionException {
        LOGGER.debug("Creating {}", this::getModelName);

        ApiUtil.invokeAndCheckResponse(() -> {
            try {
                final A apiClient = buildApiClient(getApiClass());
                final Method createMethod = apiClient.getClass().getMethod("create", getModelClass());
                return (Response) createMethod.invoke(apiClient, buildModelInstance());

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new ActionException(e);
            }
        });
    }

    protected abstract M buildModelInstance() throws ActionException;
}
