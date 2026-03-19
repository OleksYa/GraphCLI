package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.model.Graph;

/**
 * Represents a generic game command that can be executed within the system.
 * Implementing classes define specific commands that operate on a {@link Graph}.
 *
 * @author uyxbh
 */
public interface GameCommand {

    /**
     * Retrieves the name of the command.
     *
     * @return The name of the command.
     */
    String name();

    /**
     * Executes the command using the given input and graph.
     *
     * @param commandLine The command input string.
     * @param graph       The graph on which the command operates.
     * @return A {@link CommandOutput} object containing the result of the command execution.
     * @throws InvalidArgumentException If the command contains invalid arguments.
     */
    CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException;
}