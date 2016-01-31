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

import io.apiman.cli.exception.CommandException;
import io.apiman.cli.management.ManagementApiUtil;
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
public abstract class ModelCreateCommand<M, A> extends AbstractModelCommand<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelCreateCommand.class);

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("Create {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
        LOGGER.debug("Creating {}", this::getModelName);

        ManagementApiUtil.invokeAndCheckResponse(() -> {
            try {
                final A apiClient = buildServerApiClient(getApiClass());
                final Method createMethod = apiClient.getClass().getMethod("create", getModelClass());
                return (Response) createMethod.invoke(apiClient, buildModelInstance());

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new CommandException(e);
            }
        });
    }

    protected abstract M buildModelInstance() throws CommandException;
}
