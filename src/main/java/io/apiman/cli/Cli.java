package io.apiman.cli;

import com.google.common.collect.Lists;
import io.apiman.cli.api.action.AbstractAction;
import io.apiman.cli.api.action.Action;
import io.apiman.cli.core.gateway.action.GatewayAction;
import io.apiman.cli.core.org.action.OrgAction;
import io.apiman.cli.core.plugin.action.PluginAction;
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

            case "plugin":
                action = new PluginAction();
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
