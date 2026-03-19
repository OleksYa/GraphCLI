package edu.kit.kastel.exceptions;

/**
 * Read Config Exception.
 * @author uyxbh
 */
public class ReadConfigurationFileException extends Exception {
    /**
     * Parameterized constructor.
     * @param message exception message
     */
    public ReadConfigurationFileException(String message) {
        super(message);
    }

    /**
     * Parameterized constructor.
     * @param message exception message
     * @param cause inner exception
     */
    public ReadConfigurationFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
