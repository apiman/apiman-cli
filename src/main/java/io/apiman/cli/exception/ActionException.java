package io.apiman.cli.exception;

/**
 * @author Pete
 */
public class ActionException extends RuntimeException {
    public ActionException(Throwable cause) {
        super(cause);
    }

    public ActionException(String message) {
        super(message);
    }
}
