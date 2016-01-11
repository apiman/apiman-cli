package io.apiman.cli.api.action;

import java.util.List;

/**
 * @author Pete
 */
public interface Action {
    void setParent(Action action);

    void setCommand(String command);

    void run(List<String> args);

    String getCommand();

    String getActionCommandChain();
}
