/*
 * Copyright 2017 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.command.api.command;

import com.google.common.io.CharStreams;
import io.apiman.cli.command.api.ApiMixin;
import io.apiman.cli.command.api.VersionAgnosticApi;
import io.apiman.cli.command.api.model.ApiPolicy;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.exception.ExitWithCodeException;
import io.apiman.cli.management.ManagementApiUtil;
import io.apiman.cli.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

/**
 * Add a policy to an API.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ApiPolicyAddCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiPolicyAddCommand.class);

    @Option(name = "--name", aliases = {"-n"}, usage = "API name", required = true)
    private String name;

    @Option(name = "--version", aliases = {"-v"}, usage = "API version", required = true)
    private String version;

    @Option(name = "--policyName", aliases = {"-p"}, usage = "Policy name", required = true)
    private String policyName;

    @Option(name = "--configStdIn", aliases = {"-i"}, usage = "Read policy configuration from STDIN", forbids = "-f")
    private boolean configStdIn;

    @Option(name = "--configFile", aliases = {"-f"}, usage = "Policy configuration file", forbids = "-i")
    private Path configFile;

    @Inject
    public ApiPolicyAddCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected String getCommandDescription() {
        return MessageFormat.format("Add {0} policy", getModelName());
    }

    @Override
    public void performAction(CmdLineParser parser) throws CommandException {
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
                buildServerApiClient(VersionAgnosticApi.class, serverVersion).addPolicy(orgName, name, version, apiPolicy));
    }
}
