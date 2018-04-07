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

import com.beust.jcommander.Parameter;
import io.apiman.cli.command.plan.model.Plan;
import io.apiman.cli.managerapi.command.common.command.AbstractManagerModelCommand;
import io.apiman.cli.managerapi.command.plan.PlanApi;
import io.apiman.cli.managerapi.service.ManagementApiService;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public abstract class AbstractPlanCommand extends AbstractManagerModelCommand<Plan, PlanApi> {
    @Parameter(names = { "--orgName", "-o"}, description = "Organisation name", required = true)
    protected String orgName;

    public AbstractPlanCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    public Class<Plan> getModelClass() {
        return Plan.class;
    }

    public Class<PlanApi> getApiClass() {
        return PlanApi.class;
    }
}
