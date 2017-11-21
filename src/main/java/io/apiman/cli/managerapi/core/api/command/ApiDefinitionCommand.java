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

package io.apiman.cli.managerapi.core.api.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.managerapi.core.api.ApiMixin;
import io.apiman.cli.managerapi.core.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit.mime.TypedString;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Create an API.
 *
 * @author Raleigh Pickard {@literal <raleigh.pickard@gmail.com>}
 */
@Parameters(commandDescription = "Create an API using declarative definition")
public class ApiDefinitionCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiDefinitionCommand.class);

    @Parameter(names = { "--name", "-n"}, description = "API name", required = true)
    private String name;

    @Parameter(names = { "--version", "-v"}, description = "API version", required = true)
    private String version;

    @Parameter(names = { "--definitionStdIn", "-i"}, description = "Read API definition from STDIN") // , forbids = "-f"
    private boolean definitionStdIn;

    @Parameter(names = { "--definitionFile", "-f"}, description = "API definition configuration file") // , forbids = "-i")
    private Path definitionFile;

    @Parameter(names = { "--definitionType", "-t"}, description = "Endpoint", required = true)
    private String definitionType = "application/json";

    @Inject
    ApiDefinitionCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    public void performAction(JCommander parser) throws CommandException {
        if (!definitionStdIn && null == definitionFile) {
            throw new ExitWithCodeException(1, "API definition must be provided", true);
        }

        // read definition from STDIN or file
        String definition;
        try (InputStream is = (definitionStdIn ? System.in : Files.newInputStream(definitionFile))) {
            definition = CharStreams.toString(new InputStreamReader(is));

        } catch (IOException e) {
            throw new CommandException(e);
        }

        LOGGER.debug("Adding definition to API '{}' with contents: {}", this::getModelName, () -> definition);

        ManagementApiUtil.invokeAndCheckResponse(() ->
                getManagerConfig().buildServerApiClient(VersionAgnosticApi.class, serverVersion).setDefinition(orgName, name, version, definitionType, new TypedString(definition)));
    }
}
