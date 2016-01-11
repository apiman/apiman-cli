package io.apiman.cli.core.gateway.action;

import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;

import java.util.Map;

/**
 * @author Pete
 */
public class GatewayAction extends AbstractAction {
    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("create", GatewayCreateAction.class);
        actionMap.put("show", GatewayShowAction.class);
        actionMap.put("list", GatewayListAction.class);
        actionMap.put("test", GatewayTestAction.class);
    }

    @Override
    protected String getActionName() {
        return "Manage Gateways";
    }
}
