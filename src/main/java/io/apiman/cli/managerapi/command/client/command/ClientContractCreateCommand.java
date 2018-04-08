package io.apiman.cli.managerapi.command.client.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.apiman.cli.command.client.model.Contract;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.managerapi.ManagerCommon;
import io.apiman.cli.managerapi.command.client.ClientApi;
import io.apiman.cli.managerapi.management.ManagementApiUtil;
import io.apiman.cli.managerapi.service.ManagementApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Establish a contract between Client and an API via a Plan (subscribe to API)")
public class ClientContractCreateCommand extends AbstractClientCommand {
    private static final Logger LOGGER = LogManager.getLogger(ClientCreateCommand.class);

    @Parameter(names = {"--name", "-n"}, description = "Client name", required = true)
    private String name;

    @Parameter(names = {"--version", "-v"}, description = "Client version", required = true)
    private String version;

    @Parameter(names = {"--apiOrg", "-ao"}, description = "Organization of API to subscribe to")
    private String apiOrg;

    @Parameter(names = {"--apiName", "-an"}, description = "API to subscribe to", required = true)
    private String apiName;

    @Parameter(names = {"--apiVersion", "-av"}, description = "API Version to subscribe to", required = true)
    private String apiVersion;

    @Parameter(names = {"--planName", "-pn"}, description = "Name of Plan to subscribe to", required = true)
    private String planName;

    private final ManagerCommon manager;

    @Inject
    public ClientContractCreateCommand(ManagementApiService managementApiService) {
        super(managementApiService);
        manager = getManagerConfig();
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        LOGGER.debug("Creating {}", this::getModelName);

        final Contract contract = new Contract();
        contract.setApiId(apiName);
        contract.setApiOrgId(apiOrg == null ? orgName : apiOrg); // If no API org provided, then assume all in same org.
        contract.setApiVersion(apiVersion);
        contract.setPlanId(planName);

        ClientApi clientApi = manager.buildServerApiClient(ClientApi.class, manager.getServerVersion());
        ManagementApiUtil.invokeAndCheckResponse(() -> clientApi.createContract(orgName, name, version , contract));
    }
}
