package io.apiman.cli;

import com.google.common.collect.Lists;
import io.apiman.cli.action.AbstractAction;
import io.apiman.cli.action.Action;
import io.apiman.cli.action.gateway.GatewayAction;
import io.apiman.cli.action.org.OrgAction;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
public class Cli extends AbstractAction {
    public static void main(String... args) {
        new Cli().run(Lists.newArrayList(args));
    }

    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        Action action;
        switch (args.get(0)) {
            case "org":
                action = new OrgAction();
                break;

            case "gateway":
                action = new GatewayAction();
                break;

            default:
                action = null;
                break;
        }
        return action;
    }

    @Override
    protected String getActionName() {
        return "apiman-cli";
    }
}
