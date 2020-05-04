package io.github.akadir.muninn.exception;

/**
 * @author akadir
 * Date: 5.05.2020
 * Time: 22:16
 */
public class InvalidCommandException extends RuntimeException {
    private static final String MESSAGE = "Invalid command: ";


    public InvalidCommandException(String command) {
        super(MESSAGE + command);
    }

}
