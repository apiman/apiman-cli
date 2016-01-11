package io.apiman.cli.core.org.action;

import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;

import java.util.Map;

/**
 * @author Pete
 */
public class OrgAction extends AbstractAction {
    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("create", OrgCreateAction.class);
        actionMap.put("show", OrgShowAction.class);
    }

    @Override
    protected String getActionName() {
        return "Manage Organisations";
    }
}
