package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Edge;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.Product;
import edu.kit.kastel.model.RelationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;


/**
 * Command that retrieves and displays all edges (relationships) in the graph.
 * The edges are formatted as "subject -[relation]-> object" and sorted alphabetically.
 * If a node is an instance of {@link Product}, its ID is appended to the name.
 *
 * @author uyxbh
 */
public class EdgesCommand extends BaseCommand {
    private static final String COMMAND_NAME = "edges";
    private static final String NO_GRAPH_INFO_ERROR = "Error, no graph info yet.";
    private static final String UNNECESSARY_ARGUMENTS_ERROR = "Error, unnecessary arguments.";
    private static final String ID_SEPARATOR = ":";
    private static final String ARROW_OUT = "-[";
    private static final String ARROW_IN = "]->";



    /**
     * Returns the name of the command.
     *
     * @return The command name "edges".
     */
    @Override
    public String name() {
        return COMMAND_NAME;
    }

    /**
     * Executes the command to list all edges in the graph.
     *
     * @param commandLine The command input string.
     * @param graph       The graph containing nodes and edges.
     * @return A {@link CommandOutput} containing the sorted list of edges.
     * @throws InvalidArgumentException If an invalid argument is provided.
     */
    @Override
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        CommandOutput output = new CommandOutput();
        String[] params = extractCommandParams(commandLine);
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

        Map<Node, List<Edge>> nodeEdgesMap = new HashMap<>();
        for (Edge edge : graph.getEdges()) {
            if (!nodeEdgesMap.containsKey(edge.getSource())) {
                nodeEdgesMap.put(edge.getSource(), new ArrayList<>());
            }
            nodeEdgesMap.get(edge.getSource()).add(edge);
        }

        List<Node> sortedNodes = new ArrayList<>(nodeEdgesMap.keySet());
        sortedNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n1.getName().compareTo(n2.getName());
            }
        });
        for (Node node : sortedNodes) {
            List<Edge> edges = nodeEdgesMap.get(node);

            edges.sort(new Comparator<Edge>() {
                @Override
                public int compare(Edge e1, Edge e2) {
                    int nameComparison = e1.getTarget().getName().compareTo(e2.getTarget().getName());
                    if (nameComparison != 0) {
                        return nameComparison;
                    }

                    RelationType type1 = RelationType.fromString(e1.getRelation());
                    RelationType type2 = RelationType.fromString(e2.getRelation());

                    if (type1 != null && type2 != null) {
                        return Integer.compare(type1.ordinal(), type2.ordinal());
                    }

                    return 0; // If relation type is not found, keep order unchanged
                }
            });
            for (Edge edge : edges) {
                String subject = formatNode(edge.getSource());
                String object = formatNode(edge.getTarget());
                output.add(subject + ARROW_OUT + edge.getRelation() + ARROW_IN + object);
            }
        }

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