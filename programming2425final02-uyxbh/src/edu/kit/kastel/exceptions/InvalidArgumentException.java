/*
 * Copyright (c) 2024, KASTEL. All rights reserved.
 */

package edu.kit.kastel.exceptions;

/**
 * Exception that is thrown when a command argument is invalid.
 *
 * @author uyxbh
 */
public final class InvalidArgumentException extends Exception {
    private static final String ERROR_MESSAGE_FORMAT = "Error, %s";

    /**
     * Creates a new instance of the exception with the given message.
     *
     * @param message the message of the exception, consider starting it with a lower case letter.
     */
    InvalidArgumentException(String message) {
        super(ERROR_MESSAGE_FORMAT.formatted(message));
    }
}

