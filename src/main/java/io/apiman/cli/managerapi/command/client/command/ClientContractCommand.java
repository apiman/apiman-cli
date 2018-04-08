package io.apiman.cli.managerapi.command.client.command;

import com.beust.jcommander.Parameters;
import io.apiman.cli.command.core.Command;
import io.apiman.cli.managerapi.service.ManagementApiService;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Manage client contracts (APIs client is subscribed to)")
public class ClientContractCommand extends AbstractManagerCommand {

    @Inject
    public ClientContractCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("create", ClientContractCreateCommand.class);
        commandMap.put("list", ClientContractListCommand.class);
    }

}
