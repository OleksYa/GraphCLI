package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Graph;

/**
 * Represents the command to quit the game.
 * This command ensures that no additional arguments are provided and sets the appropriate status.
 *
 * @author uyxbh
 */
public class QuitGameCommand extends BaseCommand {
    private static final String QUIT_GAME_COMMAND_NAME = "quit";
    private static final String UNNECESSARY_ARGUMENTS_ERROR = "Error, there should be no arguments after command name for a quit command!";

    /**
     * Returns the name of the quit command.
     *
     * @return The command name "quit".
     */
    @Override
    public String name() {
        return QUIT_GAME_COMMAND_NAME;
    }

    /**
     * Executes the quit command, ensuring no additional arguments are provided.
     * If arguments are present, an error message is returned.
     * Otherwise, the command sets the game status to QUIT.
     *
     * @param commandLine The full command input by the user.
     * @param graph       The current game graph (not used in this command).
     * @return A CommandOutput indicating success or error.
     * @throws InvalidArgumentException If there is an issue with command execution.
     */
    @Override
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        CommandOutput output = new CommandOutput();
        String[] commandParams = extractCommandParams(commandLine);
        if (commandParams.length > 0) {
            output.add(UNNECESSARY_ARGUMENTS_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
        } else {
            output.setStatus(CommandStatus.QUIT);
        }
        return output;
    }
}
