package io.apiman.cli.api.action;

import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author Pete
 */
public abstract class AbstractFinalAction extends AbstractAction {
    /**
     * Indicates that there is no child action and this instance should handle the request.
     *
     * @param args
     * @param parser
     * @return <code>null</code>
     */
    @Override
    protected Action getChildAction(List<String> args, CmdLineParser parser) {
        return null;
    }
}
