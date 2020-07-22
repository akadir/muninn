package io.github.akadir.muninn.exception;

/**
 * @author akadir
 * Date: 22.07.2020
 * Time: 19:44
 */
public class PropertyNotLoadedException extends RuntimeException {
  private static final String MESSAGE = "Error occurred while getting properties from file. ";

  public PropertyNotLoadedException(String fileName) {
    super(MESSAGE + fileName);
  }
}
