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

package io.apiman.cli.managerapi.command.common.command;

import com.beust.jcommander.JCommander;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class ModelShowCommand<M, A> extends AbstractManagerModelCommand<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelShowCommand.class);

    public ModelShowCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Showing {}", this::getModelName);

        try {
            final A apiClient = getManagerConfig().buildServerApiClient(getApiClass());
            final Method fetchMethod = apiClient.getClass().getMethod("fetch", String.class);

            @SuppressWarnings("unchecked")
            final M model = (M) fetchMethod.invoke(apiClient, getModelId());
            LOGGER.debug("{} received: {}", this::getModelName, () -> MappingUtil.safeWriteValueAsJson(model));

            processModel(model);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    protected void processModel(M model) {
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(model));
    }

    protected abstract String getModelId() throws CommandException;
}
