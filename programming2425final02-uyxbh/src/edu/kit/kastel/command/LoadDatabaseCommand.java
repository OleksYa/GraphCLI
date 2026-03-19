package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidConfigurationException;
import edu.kit.kastel.exceptions.ReadConfigurationFileException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.helper.ConfigurationProcessor;
import edu.kit.kastel.helper.FileHelper;
import edu.kit.kastel.model.Graph;

import java.util.List;

/**
 * Implements the LoadDatabaseCommand which loads and validates a graph configuration.
 * @author uyxbh
 */
public class LoadDatabaseCommand extends BaseCommand {
    private static final String COMMAND_NAME = "load database";
    private static final String NO_FILENAME_ERROR = "Error, Missing filename.";
    private static final String FILE_NOT_READ_ERROR = "Error, Unable to read file - ";
    private static final String INVALID_CONFIGURATION_ERROR = "Error, Invalid configuration - ";
    private final ConfigurationProcessor configurationProcessor = new ConfigurationProcessor();

    @Override
    public String name() {
        return COMMAND_NAME;
    }

    @Override
    public CommandOutput execute(String commandLine, Graph graph) {
        CommandOutput output = new CommandOutput();
        String[] commandParams = extractCommandParams(commandLine);

        if (commandParams.length < 1) {
            output.add(NO_FILENAME_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }

        String filePath = commandParams[0];

        try {
            List<String> configFileLines = FileHelper.readAllLines(filePath);
            output.addLines(configFileLines);

            // Validate and build the graph
            Graph newGraph = configurationProcessor.execute(configFileLines);
            graph.replaceWith(newGraph);
        } catch (ReadConfigurationFileException e) {
            output.add(FILE_NOT_READ_ERROR + e.getMessage());
        } catch (InvalidConfigurationException e) {
            output.add(INVALID_CONFIGURATION_ERROR + e.getMessage());
        }

        output.setStatus(CommandStatus.CONTINUE);
        return output;
    }
}
