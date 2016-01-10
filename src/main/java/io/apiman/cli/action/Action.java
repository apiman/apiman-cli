package io.apiman.cli.action;

import java.util.List;

/**
 * @author Pete
 */
public interface Action {
    void run(List<String> args);
}
