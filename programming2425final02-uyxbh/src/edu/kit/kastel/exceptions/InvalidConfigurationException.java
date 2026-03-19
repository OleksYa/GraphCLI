package edu.kit.kastel.exceptions;

/**
 * Exception thrown when the configuration file contains invalid data.
 * Ensures errors are properly handled during graph validation.
 *
 * @author uyxbh
 */
public class InvalidConfigurationException extends Exception {
  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message The detail message
   */
    public InvalidConfigurationException(String message) {
        super(message);
    }
}
