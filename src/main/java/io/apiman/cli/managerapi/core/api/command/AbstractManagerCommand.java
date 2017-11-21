package io.apiman.cli.managerapi.core.api.command;

import io.apiman.cli.command.core.AbstractCommand;
import io.apiman.cli.service.ManagementApiService;

/**
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
public abstract class AbstractManagerCommand extends AbstractCommand {
    protected final ManagementApiService managementApiService;

    public AbstractManagerCommand(ManagementApiService managementApiService) {
        this.managementApiService = managementApiService;
    }
}
