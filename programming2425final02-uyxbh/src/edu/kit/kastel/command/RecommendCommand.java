package edu.kit.kastel.command;

import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.helper.ParseRecommendations;
import edu.kit.kastel.helper.RecommendationProcessor;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the recommend command.
 * Parses a term and returns recommended products.
 *
 * @author uyxbh
 */
public class RecommendCommand extends BaseCommand {
    private static final String COMMAND_NAME = "recommend";
    private static final String NO_ARGUMENTS_ERROR_MESSAGE = "Error, arguments needed.";
    private static final String ID_SEPARATOR = ":";
    private static final String RECOMMENDED_PRODUCTS_SEPARATOR = " ";
    private static final String EMPTY_STRING = "";


    @Override
    public String name() {
        return COMMAND_NAME;
    }

    @Override
    public CommandOutput execute(String commandLine, Graph graph) {
        CommandOutput output = new CommandOutput();
        String query = extractQuery(commandLine);
        if (query.isEmpty()) {
            output.add(NO_ARGUMENTS_ERROR_MESSAGE);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }

        RecommendationProcessor processor = new RecommendationProcessor(graph);
        ParseRecommendations parseRecommendations = processor.parseAndRecommend(query);

        if (parseRecommendations.getRecommendedProducts().isEmpty() && !parseRecommendations.hasError()) {
            output.add(EMPTY_STRING);
            output.setShowEmptyOutput(true);
            output.setStatus(CommandStatus.CONTINUE);
        } else if (parseRecommendations.hasError()) {
            output.add(parseRecommendations.getValidationError());
            output.setStatus(CommandStatus.CONTINUE);
        } else {
            List<Product> sortedRecommendations = new ArrayList<>(parseRecommendations.getRecommendedProducts());
            sortedRecommendations.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < sortedRecommendations.size(); i++) {
                Product p = sortedRecommendations.get(i);
                result.append(p.getName()).append(ID_SEPARATOR).append(p.getId());
                if (i < sortedRecommendations.size() - 1) {
                    result.append(RECOMMENDED_PRODUCTS_SEPARATOR);
                }
            }

            output.add(result.toString());

        }
        return output;
    }

    private String extractQuery(String commandLine) {
        return commandLine.replaceFirst(COMMAND_NAME, EMPTY_STRING).trim();
    }
}