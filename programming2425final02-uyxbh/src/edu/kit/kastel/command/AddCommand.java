package edu.kit.kastel.command;

import edu.kit.kastel.exceptions.InvalidArgumentException;
import edu.kit.kastel.helper.CommandOutput;
import edu.kit.kastel.helper.CommandStatus;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.RelationType;
import edu.kit.kastel.model.Product;
import edu.kit.kastel.model.Category;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code AddCommand} class represents the command to add relationships between nodes in the graph.
 * It ensures that valid relationships are established without duplication and follows the defined relationship types.
 *
 * @author uyxbh
 */
public class AddCommand extends BaseCommand {
    private static final String COMMAND_NAME = "add";
    private static final Pattern PRODUCT_PATTERN = Pattern.compile("^(.*?)\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\)$");
    private static final String NO_GRAPH_INFO_ERROR = "Error, no graph info yet.";
    private static final String INVALID_RELATION_ERROR = "Error, invalid relation type.";
    private static final String INVALID_PRODUCT_ERROR = "Error, product name and ID must match an existing product.";
    private static final String NON_EXISTENT_NODES_ERROR = "Error, these nodes do not exist.";
    private static final String SELF_REFERENCING_NODE_ERROR = "Error, a node cannot have a relationship with itself: ";
    private static final String DUPLICATE_ERROR = "Error, relationship already exists: ";
    private static final String ARROW = " -> ";
    private static final String CONTAINS_RELATION = "contains";
    private static final String CONTAINED_IN_RELATION = "contained-in";
    private static final String PART_OF_RELATION = "part-of";
    private static final String HAS_PART_RELATION = "has-part";
    private static final String SUCCESSOR_OF_RELATION = "successor-of";
    private static final String PREDECESSOR_OF_RELATION = "predecessor-of";

    @Override
    public String name() {
        return COMMAND_NAME;
    }

    @Override
    public CommandOutput execute(String commandLine, Graph graph) throws InvalidArgumentException {
        CommandOutput output = new CommandOutput();
        if (graph.getNodes().isEmpty()) {
            output.add(NO_GRAPH_INFO_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        String trimmedLine = commandLine.trim();
        RelationType relationType = findRelation(trimmedLine);
        if (relationType == null) {
            output.add(INVALID_RELATION_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        String relation = relationType.getRelation();
        int relationIndex = trimmedLine.indexOf(relation);
        int firstSpaceIndex = trimmedLine.indexOf(' ');
        String subjectRaw = trimmedLine.substring(firstSpaceIndex, relationIndex).trim();
        String objectRaw = trimmedLine.substring(relationIndex + relation.length()).trim();
        if (!validateProductConsistency(subjectRaw, graph) || !validateProductConsistency(objectRaw, graph)) {
            output.add(INVALID_PRODUCT_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        Node subject = graph.getNode(subjectRaw);
        Node object = graph.getNode(objectRaw);
        if (subject == null && object == null) {
            output.add(NON_EXISTENT_NODES_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        if (subject == null || object == null) {
            subject = getOrCreateNode(subjectRaw, graph);
            graph.addNode(subject);
            object = getOrCreateNode(objectRaw, graph);
            graph.addNode(object);
        }
        if (subject.equals(object)) {
            output.add(SELF_REFERENCING_NODE_ERROR + subjectRaw);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        if (isInvalidRelation(relation, subject, object)) {
            output.add(INVALID_RELATION_ERROR);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        if (graph.hasEdge(subject, object, relation)) {
            output.add(DUPLICATE_ERROR + subjectRaw + ARROW + relation + ARROW + objectRaw);
            output.setStatus(CommandStatus.CONTINUE);
            return output;
        }
        graph.addEdge(subject, object, relation);
        graph.addEdge(object, subject, relationType.getReverseRelation());
        output.setStatus(CommandStatus.CONTINUE);
        return output;
    }

    /**
     * Finds the relation word in the line.
     *
     * @param line The configuration line
     * @return The relation word if found, or null if no relation word is found
     */
    private RelationType findRelation(String line) {
        for (RelationType relationType : RelationType.values()) {
            if (line.contains(relationType.getRelation())) {
                return relationType;
            }
        }
        return null;
    }

    private Node getOrCreateNode(String nodeString, Graph graph) {
        Matcher matcher = PRODUCT_PATTERN.matcher(nodeString);

        if (matcher.matches()) {
            String name = matcher.group(1).trim().toLowerCase(); // Normalize case
            int id = Integer.parseInt(matcher.group(2).trim());

            Node existingNode = graph.getNode(name);
            return existingNode != null ? existingNode : new Product(name, id);
        }

        // If it's not a product (doesn’t match ID pattern), it's a category
        String normalizedName = nodeString.toLowerCase();
        Node existingNode = graph.getNode(normalizedName);
        return existingNode != null ? existingNode : new Category(normalizedName);
    }

    /**
     * Ensures that if a node is a product, both its name and ID must match an existing product.
     *
     * @param nodeString The raw node string (could be a product or category)
     * @param graph      The graph containing existing nodes
     * @return true if the product is valid or if it's a category, false if there's a mismatch
     */
    private boolean validateProductConsistency(String nodeString, Graph graph) {
        Node existingNode = graph.getNode(nodeString);

        if (existingNode instanceof Product) {
            Matcher matcher = PRODUCT_PATTERN.matcher(nodeString);
            if (matcher.matches()) {
                String name = matcher.group(1).trim().toLowerCase();
                int id = Integer.parseInt(matcher.group(2).trim());

                Product product = (Product) existingNode;
                return product.getName().equalsIgnoreCase(name) && product.getId() == id;
            }
        }

        return true;
    }

    private boolean isInvalidRelation(String relation, Node subject, Node object) {
        boolean isContainsRelationWithProduct = (relation.equalsIgnoreCase(CONTAINED_IN_RELATION) && object instanceof Product)
                || (relation.equalsIgnoreCase(CONTAINS_RELATION) && subject instanceof Product);

        boolean isPartOfOrSuccessorRelationWithCategory = (relation.equalsIgnoreCase(HAS_PART_RELATION)
                || relation.equalsIgnoreCase(PART_OF_RELATION)
                || relation.equalsIgnoreCase(SUCCESSOR_OF_RELATION)
                || relation.equalsIgnoreCase(PREDECESSOR_OF_RELATION))
                && (object instanceof Category || subject instanceof Category);

        return isContainsRelationWithProduct || isPartOfOrSuccessorRelationWithCategory;
    }

}