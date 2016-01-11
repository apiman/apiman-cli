package io.apiman.cli.core.plugin.action;

import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
public class PluginAction extends AbstractAction {
    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        Action action;
        switch (args.get(0)) {
            case "create":
                action = new PluginCreateAction();
                break;

            case "show":
                action = new PluginShowAction();
                break;

            case "list":
                action = new PluginListAction();
                break;

            default:
                action = null;
                break;
        }
        return action;
    }

    @Override
    protected String getActionName() {
        return "Manage Plugins";
    }
}
