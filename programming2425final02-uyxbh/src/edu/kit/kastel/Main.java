/*
 * Copyright (c) 2024, KASTEL. All rights reserved.
 */

package edu.kit.kastel;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.UserInterface;

import java.util.Map;
import java.util.Scanner;

/**
 * The main class of the application, which starts the user interface.
 *
 * @author uyxbh
 */
public final class Main {
    private static final Map<String, String> SPECIAL_ESCAPE_SEQUENCES = Map.of(System.lineSeparator(), "%n");
    private static final String CONTROL_ESCAPE_SEQUENCE = "\\u%04x";

    private Main() {
    }

    /**
     * The main method of the application.
     *
     * @param args command line arguments are ignored.
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserInterface userInterface = new UserInterface(scanner);
            userInterface.run(args);
        } catch (InvalidArgumentException exception) {
            System.err.println(escape(exception.getMessage()));
        }
    }

    private static String escape(String input) {
        StringBuilder result = new StringBuilder();

        String value = input;
        for (Map.Entry<String, String> entry : SPECIAL_ESCAPE_SEQUENCES.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        for (char letter : value.toCharArray()) {
            if (Character.isISOControl(letter)) {
                result.append(CONTROL_ESCAPE_SEQUENCE.formatted((int) letter));
            } else {
                result.append(letter);
            }
        }

        return result.toString();
    }
}
