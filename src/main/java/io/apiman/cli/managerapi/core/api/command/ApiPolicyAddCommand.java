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
import io.apiman.cli.managerapi.core.api.ApiMixin;
import io.apiman.cli.managerapi.core.api.VersionAgnosticApi;
import io.apiman.cli.core.api.model.ApiPolicy;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Add a policy to an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
@Parameters(commandDescription = "Add policy to API")
public class ApiPolicyAddCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiPolicyAddCommand.class);

    @Parameter(names = { "--name", "-n" }, description = "API name", required = true)
    private String name;

    @Parameter(names = { "--version", "-v" }, description = "API version", required = true)
    private String version;

    @Parameter(names = { "--policyName", "-p" }, description = "Policy name", required = true)
    private String policyName;

    @Parameter(names = { "--configStdIn", "-i" }, description = "Read policy configuration from STDIN") // TODO forbids -f
    private boolean configStdIn;

    @Parameter(names = { "--configFile", "-f" }, description = "Policy configuration file") // TODO forbids i
    private Path configFile;

    @Override
    public void performAction(JCommander parser) throws CommandException {
        if (!configStdIn && null == configFile) {
            throw new ExitWithCodeException(1, "Policy configuration must be provided", true);
        }

        // read configuration from STDIN or file
        String policyConfig;
        try (InputStream is = (configStdIn ? System.in : Files.newInputStream(configFile))) {
            policyConfig = CharStreams.toString(new InputStreamReader(is));

        } catch (IOException e) {
            throw new CommandException(e);
        }

        LOGGER.debug("Adding policy '{}' to API '{}' with configuration: {}",
                () -> policyName, this::getModelName, () -> policyConfig);

        final ApiPolicy apiPolicy = new ApiPolicy(policyName);
        apiPolicy.setDefinitionId(policyName);

        ManagementApiUtil.invokeAndCheckResponse(() ->
                getManagerConfig().buildServerApiClient(VersionAgnosticApi.class, serverVersion).addPolicy(orgName, name, version, apiPolicy));
    }
}
