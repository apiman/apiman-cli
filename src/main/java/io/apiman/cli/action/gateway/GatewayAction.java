package io.apiman.cli.action.gateway;

import io.apiman.cli.action.AbstractAction;
import io.apiman.cli.action.Action;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
public class GatewayAction extends AbstractAction {
    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        Action action;
        switch (args.get(0)) {
            case "create":
                action = new GatewayCreateAction();
                break;

            case "show":
                action = new GatewayShowAction();
                break;

            case "list":
                action = new GatewayListAction();
                break;

            default:
                action = null;
                break;
        }
        return action;
    }

    @Override
    protected String getActionName() {
        return "Manage gateway";
    }
}
