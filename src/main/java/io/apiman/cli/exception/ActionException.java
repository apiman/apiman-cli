package io.apiman.cli.exception;

/**
 * @author Pete
 */
public class ActionException extends Exception {
    public ActionException(Throwable cause) {
        super(cause);
    }

    public ActionException(String message) {
        super(message);
    }
}
