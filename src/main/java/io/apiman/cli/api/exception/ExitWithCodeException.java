package io.apiman.cli.api.exception;

/**
 * @author Pete
 */
public class ExitWithCodeException extends RuntimeException {
    private final int exitCode;

    public ExitWithCodeException(int exitCode, String message) {
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
