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

package io.apiman.cli.managerapi.command;

import com.beust.jcommander.JCommander;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public abstract class ModelListCommand<M, A> extends AbstractManagerModelCommand<M, A> {
    private static final Logger LOGGER = LogManager.getLogger(ModelListCommand.class);

    public ModelListCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected boolean permitNoArgs() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void performAction(JCommander parser) throws CommandException {
        LOGGER.debug("Listing {}", this::getModelName);

        try {
            final A apiClient = getManagerConfig().buildServerApiClient(getApiClass());
            final Method listMethod = apiClient.getClass().getMethod("list");
            processList((List<M>) listMethod.invoke(apiClient));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new CommandException(e);
        }
    }

    protected void processList(List<M> model) {
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(model));
    }
}
