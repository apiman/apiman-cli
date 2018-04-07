/*
 * Copyright 2018 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.managerapi.command.plan.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.command.plan.model.Plan;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.plan.PlanApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.managerapi.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Create a Plan")
public class PlanCreateCommand extends AbstractPlanCommand {
    private static final Logger LOGGER = LogManager.getLogger(PlanCreateCommand.class);

    @Parameter(names = {"--name", "-n"}, description = "Plan name", required = true)
    private String name;

    @Parameter(names = {"--description", "-d"}, description = "Description")
    private String description;

    @Parameter(names = {"--initialVersion", "-v"}, description = "Initial version", required = true)
    private String initialVersion;

    @Inject
    public PlanCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Creating {}", this::getModelName);

        final Plan plan = new Plan(
                name,
                description,
                initialVersion);

        // create
        final PlanApi planClient = getManagerConfig().buildServerApiClient(PlanApi.class);
        ManagementApiUtil.invokeAndCheckResponse(() -> planClient.create(orgName, plan));
    }
}
