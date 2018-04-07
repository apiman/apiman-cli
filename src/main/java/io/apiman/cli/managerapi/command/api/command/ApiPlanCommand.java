package io.apiman.cli.managerapi.command.api.command;

import com.beust.jcommander.Parameters;
import io.apiman.cli.command.core.Command;
import io.apiman.cli.managerapi.AbstractManagerCommand;
import io.apiman.cli.managerapi.service.ManagementApiService;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@Parameters(commandDescription = "Manage Plans available on APIs")
public class ApiPlanCommand extends AbstractManagerCommand {

    @Inject
    public ApiPlanCommand(ManagementApiService managementApiService) {
        super(managementApiService);
    }

    @Override
    protected void populateCommands(Map<String, Class<? extends Command>> commandMap) {
        commandMap.put("add", ApiPlanAddCommand.class);
        //commandMap.put("remove", ApiPlanRemoveCommand.class);
        //commandMap.put("list", ApiPlanListCommand.class);
    }

}
