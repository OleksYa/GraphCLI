package edu.kit.kastel.command;

import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Category;
import edu.kit.kastel.model.Edge;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.RelationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the export command to output the graph in DOT notation.
 * Follows the rules specified in section A.5.
 *
 * @author uyxbh
 */
public class ExportCommand extends BaseCommand {
    private static final String COMMAND_NAME = "export";
    private static final String ERROR_UNNECESSARY_ARGUMENTS = "Error, unnecessary arguments.";
    private static final String ERROR_NO_GRAPH_INFO = "Error, no graph info yet.";
    private static final String DIGRAPH_START = "digraph {\n";
    private static final String DIGRAPH_END = "}";
    private static final String SHAPE_BOX_FORMAT = " [shape=box]\n";
    private static final String EDGE_FORMAT = "%s -> %s [label=%s]\n";
    private static final String HYPHEN = "-";
    private static final String EMPTY_STRING = "";

    @Override
    public String name() {
        return COMMAND_NAME;
    }

    @Override
    public CommandOutput execute(String commandLine, Graph graph) {
        CommandOutput output = new CommandOutput();
        String[] params = extractCommandParams(commandLine);

        if (params.length != 0) {
            output.add(ERROR_UNNECESSARY_ARGUMENTS);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        if (graph.getNodes().isEmpty()) {
            output.add(ERROR_NO_GRAPH_INFO);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }

        StringBuilder dotRepresentation = new StringBuilder();
        dotRepresentation.append(DIGRAPH_START);

        List<Edge> edges = new ArrayList<>(graph.getEdges());
        edges.sort(this::compareEdges);

        for (Edge edge : edges) {
            dotRepresentation.append(formatEdge(edge));
        }

        List<String> categoryLines = new ArrayList<>();
        for (Node node : graph.getNodes()) {
            if (node instanceof Category && !graph.isNodeIsolated(node)) {
                categoryLines.add(node.getName() + SHAPE_BOX_FORMAT);
            }
        }
        Collections.sort(categoryLines);
        for (String categoryLine : categoryLines) {
            dotRepresentation.append(categoryLine);
        }

        dotRepresentation.append(DIGRAPH_END);

        output.add(dotRepresentation.toString());
        return output;
    }

    /**
     * Formats an edge for DOT notation.
     * Removes hyphens from the relationship type for the DOT label.
     */
    private String formatEdge(Edge edge) {
        String source = edge.getSource().getName();
        String target = edge.getTarget().getName();
        String relation = edge.getRelation().replace(HYPHEN, EMPTY_STRING); // Remove hyphens for DOT syntax

        return String.format(EDGE_FORMAT, source, target, relation);
    }

    private int compareEdges(Edge e1, Edge e2) {
        int sourceCompare = e1.getSource().getName().compareTo(e2.getSource().getName());
        if (sourceCompare != 0) {
            return sourceCompare;
        }

        int targetCompare = e1.getTarget().getName().compareTo(e2.getTarget().getName());
        if (targetCompare != 0) {
            return targetCompare;
        }

        RelationType type1 = RelationType.fromString(e1.getRelation());
        RelationType type2 = RelationType.fromString(e2.getRelation());

        if (type1 != null && type2 != null) {
            return Integer.compare(type1.ordinal(), type2.ordinal()); // Sort by enum order
        }

        return e1.getRelation().compareTo(e2.getRelation()); // Fallback for unknown relations
    }
}
