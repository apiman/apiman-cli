package io.apiman.cli.managerapi.service;

import io.apiman.cli.managerapi.command.common.ActionApi;
import io.apiman.cli.managerapi.command.common.util.ServerActionUtil;
import io.apiman.cli.managerapi.command.plan.PlanApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;

public class PlanServiceImpl implements PlanService {
    private static final Logger LOGGER = LogManager.getLogger(PlanServiceImpl.class);

    private ManagementApiService managementApiService;

    @Inject
    public PlanServiceImpl(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }

    @Override
    public String fetchCurrentState(String orgName, String planName, String planVersion) {
        final PlanApi planClient = managementApiService.buildServerApiClient(PlanApi.class);
        final String planState = ofNullable(planClient.fetchVersion(orgName, planName, planVersion).getStatus()).orElse("");
        LOGGER.debug("Plan '{}' state: {}", planName, planState);
        return planState;
    }

    @Override
    public void lock(String orgName, String planName, String planVersion) {
        String state = fetchCurrentState(orgName, planName, planVersion);
        // Only lock if in valid state
        if (state.equalsIgnoreCase(PlanService.STATE_READY) || state.equalsIgnoreCase(PlanService.STATE_CREATED)) {
            LOGGER.info("Locking Plan: {}", planName);
            ServerActionUtil.lockPlan(orgName, planName, planVersion,
                    managementApiService.buildServerApiClient(ActionApi.class));
        } else if (state.equalsIgnoreCase(PlanService.STATE_LOCKED)) {
            LOGGER.info("Plan {} {} already locked.",
                    planName, planVersion);
        }
    }
}
