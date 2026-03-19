package edu.kit.kastel.helper;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.model.Graph;

import java.util.Scanner;

/**
 * The main class for interacting with the user.
 * Handles input processing and command execution.
 *
 * @author uyxbh
 */
public final class UserInterface {
    private static final CommandProcessor COMMAND_PROCESSOR = new CommandProcessor();

    private final Scanner scanner;
    private final Graph graph;

    /**
     * Creates a new UserInterface.
     *
     * @param scanner the scanner used to read user input. The caller should close this scanner.
     */
    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
        this.graph = new Graph();
    }

    /**
     * Starts the user interface and only returns when the user wants to exit the application.
     * @param args command line arguments
     * @throws InvalidArgumentException if arguments are invalid
     */
    public void run(String[] args) throws InvalidArgumentException {
        CommandOutput output = COMMAND_PROCESSOR.processStartupArguments(args, graph);
        showOutput(output.toString(), false);

        do {
            try {
                String commandWithArguments = this.scanner.nextLine();
                output = COMMAND_PROCESSOR.execute(commandWithArguments, graph);
                showOutput(output.toString(), output.getShowEmptyOutput());

            } catch (InvalidArgumentException exception) {
                System.err.println(exception.getMessage());
            }
        } while (isContinue(output));
    }

    private void showOutput(String outputText, boolean showEmptyLine) {
        if (outputText != null) {
            if (!outputText.isEmpty()) {
                System.out.println(outputText);
            } else {
                if (showEmptyLine) {
                    System.out.println(outputText);
                }
            }
        }
    }

    private boolean isContinue(CommandOutput output) {
        return output != null && output.getStatus() != CommandStatus.QUIT;
    }
}