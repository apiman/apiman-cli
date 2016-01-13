package io.apiman.cli.exception;

/**
 * @author pete
 */
public class DeclarativeException extends RuntimeException {
    public DeclarativeException() {
        super();
    }

    public DeclarativeException(Throwable cause) {
        super(cause);
    }

    public DeclarativeException(String message) {
        super(message);
    }

    public DeclarativeException(String message, Throwable cause) {
        super(message, cause);
    }
}
