package io.apiman.cli.action.org;

import io.apiman.cli.action.AbstractAction;
import io.apiman.cli.action.Action;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
public class OrgAction extends AbstractAction {
    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        Action action;
        switch (args.get(0)) {
            case "create":
                action = new OrgCreateAction();
                break;

            case "show":
                action = new OrgShowAction();
                break;

            default:
                action = null;
                break;
        }
        return action;
    }

    @Override
    protected String getActionName() {
        return "Manage organisation";
    }
}
