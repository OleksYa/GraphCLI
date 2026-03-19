package edu.kit.kastel.command;

/**
 * An abstract base class for all game commands. Provides a utility method to extract command parameters
 * from the input command line.
 *
 * This class ensures that commands correctly interpret and process their arguments by removing
 * the command name and splitting the remaining input into parameters.
 *
 * Classes extending {@code BaseCommand} must implement the {@link GameCommand} interface.
 *
 * @author uyxbh
 */
public abstract class BaseCommand implements GameCommand {
    private static final String COMMAND_PARTS_SEPARATOR = " ";

    /**
     * Extracts command parameters from the given command line input.
     *
     * This method first verifies that the command line starts with the expected command name.
     * If the command name is correct, it removes it from the string and extracts the parameters.
     * If no parameters are found, an empty array is returned.
     *
     * @param commandLine The full command input provided by the user.
     * @return An array of command parameters, or an empty array if no parameters exist.
     */
    protected String[] extractCommandParams(String commandLine) {
        String commandName = this.name();  // Get the command's name
        String trimmedCommand = commandLine.trim();

        // Ensure the command line starts with the correct command name
        if (!trimmedCommand.startsWith(commandName)) {
            return new String[0]; // Return empty if the command name doesn't match
        }

        // Remove the command name and extract the parameters
        String paramsPart = trimmedCommand.substring(commandName.length()).trim();
        return paramsPart.isEmpty() ? new String[0] : paramsPart.split(COMMAND_PARTS_SEPARATOR);
    }
}