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

package io.apiman.cli.core.api.command;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import io.apiman.cli.core.api.ApiMixin;
import io.apiman.cli.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.*;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.management.ManagementApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import retrofit.mime.TypedString;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

/**
 * Create an API.
 *
 * @author Raleigh Pickard {@literal <raleigh.pickard@gmail.com>}
 */
public class ApiDefinitionCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiDefinitionCommand.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "API name", required = true)
    private String name;

    @Option(name = "--version", aliases = {"-v"}, usage = "API version", required = true)
    private String version;

    @Option(name = "--definitionStdIn", aliases = {"-i"}, usage = "Read API definition from STDIN", forbids = "-f")
    private boolean definitionStdIn;

    @Option(name = "--definitionFile", aliases = {"-f"}, usage = "API definition configuration file", forbids = "-i")
    private Path definitionFile;

    @Option(name = "--definitionType", aliases = {"-t"}, usage = "Endpoint", required = true)
    private String definitionType = "application/json";

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("Set definition {0}", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
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
                buildServerApiClient(VersionAgnosticApi.class, serverVersion).setDefinition(orgName, name, version, definitionType, new TypedString(definition)));
    }
}
