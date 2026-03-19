package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a command that lists all nodes in the graph.
 * Nodes are sorted alphabetically, and product nodes include their IDs.
 *
 * @author uyxbh
 */
public class NodesCommand extends BaseCommand {
    private static final String COMMAND_NAME = "nodes";
    private static final String ID_SEPARATOR = ":";
    private static final String NODE_SEPARATOR = " ";
    private static final String UNNECESSARY_ARGUMENTS_ERROR = "Error, unnecessary arguments.";
    private static final String NO_GRAPH_INFO_ERROR = "Error, no graph info yet.";


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
     * Executes the command to retrieve and display all nodes in the graph.
     * Nodes that are instances of {@link Product} include their IDs.
     *
     * @param commandLine The command input string.
     * @param graph       The graph containing the nodes.
     * @return A {@link CommandOutput} object containing the sorted list of nodes.
     * @throws InvalidArgumentException If the command contains invalid arguments.
     */
    @Override
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        CommandOutput output = new CommandOutput();
        String[] params = extractCommandParams(commandLine);
        List<String> nodeList = new ArrayList<>();
        if (params.length != 0) {
            output.add(UNNECESSARY_ARGUMENTS_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        if (graph.getNodes().isEmpty()) {
            output.add(NO_GRAPH_INFO_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        for (Node node : graph.getNodes()) {
            if (graph.isNodeIsolated(node)) {
                nodeList = nodeList; //redundant assignment forced by checkstyle
            } else {
                nodeList.add(formatNode(node));
            }
        }

        // Sort nodes: First by name, then by numeric ID if applicable
        nodeList.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                // Split into name and optional ID part
                String[] parts1 = s1.split(ID_SEPARATOR);
                String[] parts2 = s2.split(ID_SEPARATOR);

                // Compare by name first
                int nameCompare = parts1[0].compareTo(parts2[0]);
                if (nameCompare != 0) {
                    return nameCompare;
                }

                // If names are the same, compare numeric IDs
                if (parts1.length > 1 && parts2.length > 1) {
                    int id1 = Integer.parseInt(parts1[1]);
                    int id2 = Integer.parseInt(parts2[1]);
                    return Integer.compare(id1, id2);
                }

                return 0;
            }
        });

        output.add(String.join(NODE_SEPARATOR, nodeList));
        output.setStatus(CommandStatus.CONTINUE);
        return output;
    }

    private String formatNode(Node node) {
        if (node instanceof Product product) {
            return product.getName() + ID_SEPARATOR + product.getId();
        }
        return node.getName();
    }
}