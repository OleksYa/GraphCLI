package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.RelationType;

/**
 * Represents a command to remove an existing relationship between two nodes in the graph.
 * The relationship is removed in both directions (including the reverse relation).
 *
 * @author uyxbh
 */
public class RemoveCommand extends BaseCommand {
    private static final String COMMAND_NAME = "remove";
    private static final String MISSING_COMMAND_PART_ERROR_MESSAGE = "Error, missing subject, predicate, or object.";
    private static final String INVALID_RELATION_ERROR_MESSAGE = "Error, invalid relation type.";
    private static final String NO_RELATION_TO_DELETE_ERROR_MESSAGE = "Error, relationship does not exist.";
    private static final char SPACE = ' ';

    /**
     * Retrieves the name of the command.
     *
     * @return The name of the command.
     */
    @Override
    public String name() {
        return COMMAND_NAME;
    }

    /**
     * Executes the command to remove a relationship between two nodes.
     * Ensures that the relation exists before attempting removal.
     *
     * @param commandLine The command input string.
     * @param graph       The graph containing the nodes and relationships.
     * @return A {@link CommandOutput} object indicating success or failure.
     * @throws InvalidArgumentException If the command contains invalid arguments.
     */
    @Override
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        CommandOutput output = new CommandOutput();
        String[] params = extractCommandParams(commandLine);

        if (params.length < 3) {
            output.add(MISSING_COMMAND_PART_ERROR_MESSAGE);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }

        String trimmedLine = commandLine.trim();

        RelationType relationType = findRelation(trimmedLine);
        if (relationType == null) {
            output.add(INVALID_RELATION_ERROR_MESSAGE);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }

        // Extract the relation string to use as a delimiter
        String relation = relationType.getRelation();

        int relationIndex = trimmedLine.indexOf(relation);

        int firstSpaceIndex = trimmedLine.indexOf(SPACE);
        String subjectRaw = trimmedLine.substring(firstSpaceIndex, relationIndex).trim();
        String objectRaw = trimmedLine.substring(relationIndex + relation.length()).trim();

        Node subject = graph.getNode(subjectRaw);
        Node object = graph.getNode(objectRaw);

        // Check if the relationship exists before attempting removal
        if (subject == null || object == null || !graph.hasEdge(subject, object, relation)) {
            output.add(NO_RELATION_TO_DELETE_ERROR_MESSAGE);
        } else {
            graph.removeEdge(subject, object, relation);
            graph.removeEdge(object, subject, relationType.getReverseRelation());
        }

        output.setStatus(CommandStatus.CONTINUE);
        return output;
    }

    /**
     * Finds the relation word in the input line.
     *
     * @param line The input command line.
     * @return The corresponding {@link RelationType} if found, or null if no relation type is detected.
     */
    private RelationType findRelation(String line) {
        for (RelationType relationType : RelationType.values()) {
            if (line.contains(relationType.getRelation())) {
                return relationType;
            }
        }
        return null; // If no relation is found
    }
}