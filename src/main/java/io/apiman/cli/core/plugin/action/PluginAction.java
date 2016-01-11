package io.apiman.cli.core.plugin.action;

import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;

import java.util.Map;

/**
 * @author Pete
 */
public class PluginAction extends AbstractAction {
    @Override
    protected void populateActions(Map<String, Class<? extends Action>> actionMap) {
        actionMap.put("create", PluginCreateAction.class);
        actionMap.put("show", PluginShowAction.class);
        actionMap.put("list", PluginListAction.class);
    }

    @Override
    protected String getActionName() {
        return "Manage Plugins";
    }
}
