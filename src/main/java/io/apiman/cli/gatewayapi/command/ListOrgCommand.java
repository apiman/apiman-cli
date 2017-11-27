/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.cli.gatewayapi.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import io.apiman.cli.annotations.CommandAvailableSince;
import io.apiman.cli.exception.CommandException;
import io.apiman.cli.gatewayapi.GatewayApi;
import io.apiman.cli.gatewayapi.GatewayHelper;
import io.apiman.cli.gatewayapi.command.common.AbstractGatewayCommand;
import io.apiman.cli.gatewayapi.command.factory.GatewayApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * List all Org IDs on gateway.
 *
 * @author Marc Savy {@literal <marc@rhymewithgravy.com>}
 */
@CommandAvailableSince("1.3.2")
@Parameters(commandDescription = "List all Organization IDs")
public class ListOrgCommand extends AbstractGatewayCommand
        implements GatewayHelper {

    private Logger LOGGER = LogManager.getLogger(ListOrgCommand.class);

    @Inject
    protected ListOrgCommand(GatewayApiService apiService) {
        super(apiService);
    }

    @Override
    public void performFinalAction(JCommander parser) throws CommandException {
        GatewayApi gatewayApi = getGatewayApiService().buildGatewayApiClient();
        // Do status check
        statusCheck(gatewayApi, getGatewayConfig().getGatewayApiEndpoint());
        // Get endpoint (if any)
        List<String> orgs = callAndCatch(() -> gatewayApi.listOrgs());
        LOGGER.debug("Orgs returned: {}", orgs.size());
        // Sort case insensitively
        orgs.sort(String::compareToIgnoreCase);
        orgs.forEach(System.out::println);
    }

    protected boolean permitNoArgs() {
        return true;
    }
}
