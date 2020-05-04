package io.github.akadir.muninn.exception;

/**
 * @author akadir
 * Date: 3.05.2020
 * Time: 16:18
 */
public class PropertyNotLoadedException extends RuntimeException {
    private static final String MESSAGE = "Error occurred while getting properties from file: ";

    public PropertyNotLoadedException(String fileName) {
        super(MESSAGE + fileName);
    }

    public PropertyNotLoadedException(String fileName, Exception e) {
        super(MESSAGE + fileName, e);
    }
}
