package io.apiman.cli.api.action;

import java.util.List;

/**
 * @author Pete
 */
public interface Action {
    void setParent(Action action);

    void setCommand(String command);

    String getCommand();

    /**
     * Parse the given arguments and perform an action.
     *
     * @param args the arguments to parse
     */
    void run(List<String> args);

    /**
     * @return a concatenation of the parent's action command and this action command
     */
    String getCommandChain();
}
