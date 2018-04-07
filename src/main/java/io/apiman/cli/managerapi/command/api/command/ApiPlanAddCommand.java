package io.apiman.cli.managerapi.command.api.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.command.api.model.ApiConfig;
import io.apiman.cli.command.api.model.ApiPlan;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.api.ApiMixin;
import io.apiman.cli.managerapi.command.api.VersionAgnosticApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.managerapi.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Add Plan to API")
public class ApiPlanAddCommand extends AbstractApiCommand implements ApiMixin {
    private static final Logger LOGGER = LogManager.getLogger(ApiPlanAddCommand.class);

    @Parameter(names = { "--name", "-n" }, description = "API name", required = true)
    private String apiName;

    @Parameter(names = { "--version", "-v" }, description = "API version", required = true)
    private String apiVersion;

    @Parameter(names = { "--planName", "-p" }, description = "Plan name", required = true)
    private String planName;

    @Parameter(names = { "--planVersion", "-w" }, description = "Plan Version", required = true)
    private String planVersion;

    @Inject
    public ApiPlanAddCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Adding Plan '{}' to API '{}'", () -> planName, this::getModelName);

        VersionAgnosticApi api = getManagerConfig()
                .buildServerApiClient(VersionAgnosticApi.class, getManagerConfig().getServerVersion());

        ManagementApiUtil.invokeAndCheckResponse(() -> {
            ApiConfig conf = api.fetchVersionConfig(orgName, apiName, apiVersion);

            // Add the plan
            conf.getPlans().add(new ApiPlan(planName, planVersion));

            return api.configure(orgName, apiName, apiVersion, conf);
        });
    }

}
