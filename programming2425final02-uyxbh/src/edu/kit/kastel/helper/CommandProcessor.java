package edu.kit.kastel.helper;

import edu.kit.kastel.command.EdgesCommand;
import edu.kit.kastel.command.RemoveCommand;
import edu.kit.kastel.command.ExportCommand;
import edu.kit.kastel.command.RecommendCommand;
import edu.kit.kastel.command.LoadDatabaseCommand;
import edu.kit.kastel.command.AddCommand;
import edu.kit.kastel.command.GameCommand;
import edu.kit.kastel.command.NodesCommand;
import edu.kit.kastel.command.QuitGameCommand;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.exceptions.InvalidArgumentException;

import java.util.List;

/**
 * Implements Command Processor.
 * This class handles command execution and lookup.
 *
 * @author uyxbh
 */
public class CommandProcessor {
    private static final String LOAD_CONFIG_COMMAND_NAME = "load";
    private static final String LOAD_CONFIG_FORMAT = "%s %s";
    private static final String UNABLE_TO_LOAD_ERROR = "Error while loading the file %s";
    private static final String UNKNOWN_COMMAND_ERROR = "Error, unknown command \"%s\"";
    private static final String COMMAND_NAME_SEPARATOR = "\\s";

    private static final List<GameCommand> GAME_COMMANDS = List.of(
            new QuitGameCommand(),
            new LoadDatabaseCommand(),
            new NodesCommand(),
            new AddCommand(),
            new EdgesCommand(),
            new RemoveCommand(),
            new ExportCommand(),
            new RecommendCommand()
    );

    /**
     * Process startup Arguments.
     * @param args from the command line
     * @param graph object reference
     * @return Command Output
     */
    public CommandOutput processStartupArguments(String[] args, Graph graph) {
        CommandOutput commandOutput = new CommandOutput();
        if (args.length == 0) {
            return commandOutput;
        }

        String filePath = args[0];
        try {
            String commandLine = LOAD_CONFIG_FORMAT.formatted(LOAD_CONFIG_COMMAND_NAME, filePath);
            GameCommand command = this.getCommand(LOAD_CONFIG_COMMAND_NAME);
            if (command != null) {
                CommandOutput output = command.execute(commandLine, graph);
                commandOutput.add(output.toString());
            }
        } catch (InvalidArgumentException exception) {
            commandOutput.add(UNABLE_TO_LOAD_ERROR.formatted(filePath));
        }

        return commandOutput;
    }

    /**
     * Process command line.
     * @param commandLine string
     * @param graph reference to graph
     * @return command output
     * @throws InvalidArgumentException if the command is invalid
     */
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        GameCommand command = getCommand(commandLine);
        CommandOutput output = new CommandOutput();
        if (command != null) {
            output = command.execute(commandLine, graph);
        } else {
            output.add(UNKNOWN_COMMAND_ERROR.formatted(commandLine.split(COMMAND_NAME_SEPARATOR)[0]));
        }
        return output;
    }

    private GameCommand getCommand(String commandLine) {
        for (GameCommand command : GAME_COMMANDS) {
            if (commandLine.toLowerCase().startsWith(command.name())) {
                return command;
            }
        }
        return null;
    }
}