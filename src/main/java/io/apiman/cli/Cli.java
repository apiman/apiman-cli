package io.apiman.cli;

import com.google.common.collect.Lists;
import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;
import io.apiman.cli.core.gateway.action.GatewayAction;
import io.apiman.cli.core.org.action.OrgAction;
import io.apiman.cli.core.plugin.action.PluginAction;
import io.apiman.cli.core.service.action.ServiceAction;

import java.util.Map;

/**
 * @author Pete
 */
public class Cli extends AbstractAction {
    public static void main(String... args) {
        new Cli().run(Lists.newArrayList(args));
    }

    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("org", OrgAction.class);
        actionMap.put("gateway", GatewayAction.class);
        actionMap.put("plugin", PluginAction.class);
        actionMap.put("service", ServiceAction.class);
    }

    @Override
    protected String getActionName() {
        return "apiman-cli";
    }

    @Override
    public String getCommand() {
        return "apiman";
    }
}
