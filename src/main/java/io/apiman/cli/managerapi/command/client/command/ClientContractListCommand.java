package io.apiman.cli.managerapi.command.client.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import io.apiman.cli.command.client.model.Contract;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.command.plan.command.PlanListCommand;
import io.apiman.cli.managerapi.service.ManagementApiService;
import io.apiman.cli.util.LogUtil;
import io.apiman.cli.util.MappingUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public class ClientContractListCommand extends AbstractClientCommand {
    private static final Logger LOGGER = LogManager.getLogger(PlanListCommand.class);

    @Parameter(names = {"--name", "-n"}, description = "Client name", required = true)
    private String clientName;

    @Parameter(names = {"--version", "-v"}, description = "Client version", required = true)
    private String clientVersion;

    @Inject
    public ClientContractListCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Listing {}", this::getModelName);

        final List<Contract> plans = getManagerConfig()
                .buildServerApiClient(ClientApi.class, getManagerConfig().getServerVersion())
                .listContracts(orgName, clientName, clientVersion);
        LogUtil.OUTPUT.info(MappingUtil.safeWriteValueAsJson(plans));
    }
}
