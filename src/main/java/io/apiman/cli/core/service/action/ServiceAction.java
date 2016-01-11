package io.apiman.cli.core.service.action;

import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;

import java.util.Map;

/**
 * @author Pete
 */
public class ServiceAction extends AbstractAction {
    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("create", ServiceCreateAction.class);
        actionMap.put("publish", ServicePublishAction.class);
    }

    @Override
    protected String getActionName() {
        return "Manage Services";
    }
}
