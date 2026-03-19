package edu.kit.kastel.helper;

import edu.kit.kastel.exceptions.InvalidConfigurationException;
import edu.kit.kastel.model.Graph;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.Product;
import edu.kit.kastel.model.RelationType;
import edu.kit.kastel.model.Category;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes and validates the configuration for the graph.
 * Ensures valid nodes, edges, and relationships.
 *
 * @author uyxbh
 */
public class ConfigurationProcessor {
    private static final Pattern PRODUCT_PATTERN = Pattern.compile("^(.*?)\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\)$");

    private static final String ERROR_RELATION_NOT_FOUND = "Invalid format. Relation not found.";
    private static final String ERROR_MULTIPLE_PRODUCT_IDS = "several products share an id";
    private static final String ERROR_PRODUCTS_CONTAIN = "products can not contain anything";
    private static final String ERROR_CATEGORIES_PARTS = "categories cannot have parts"
            + " or be in a successor/predecessor relationship.";
    private static final String ERROR_SELF_RELATIONSHIP = "A node cannot have a relationship with itself: ";
    private static final String ERROR_DUPLICATE_RELATIONSHIP = "Duplicate relationship detected: ";
    private static final String CONTAINED_IN = "contained-in";
    private static final String CONTAINS = "contains";
    private static final String HAS_PART = "has-part";
    private static final String PART_OF = "part-of";
    private static final String SUCCESSOR_OF = "successor-of";
    private static final String PREDECESSOR_OF = "predecessor-of";
    private static final String ARROW_SIGN = " -> ";

    /**
     * Parses and processes the given configuration lines into a Graph.
     *
     * @param configLines List of configuration lines
     * @return Validated Graph object
     * @throws InvalidConfigurationException If the configuration is invalid
     */
    public Graph execute(List<String> configLines) throws InvalidConfigurationException {
        Graph graph = new Graph();

        for (String line : configLines) {
            processLine(line, graph);
        }

        return graph;
    }

    /**
     * Processes a single line from the configuration file and adds the appropriate nodes and edges.
     *
     * @param line  Configuration line
     * @param graph Graph object to modify
     * @throws InvalidConfigurationException If the line format is invalid
     */
    private void processLine(String line, Graph graph) throws InvalidConfigurationException {
        String trimmedLine = line.trim();

        RelationType relationType = findRelation(trimmedLine);
        if (relationType == null) {
            throw new InvalidConfigurationException(ERROR_RELATION_NOT_FOUND);
        }

        String relation = relationType.getRelation();

        int relationIndex = trimmedLine.indexOf(relation);
        if (relationIndex == -1) {
            throw new InvalidConfigurationException(ERROR_RELATION_NOT_FOUND);
        }

        String subjectRaw = trimmedLine.substring(0, relationIndex).trim();
        String objectRaw = trimmedLine.substring(relationIndex + relation.length()).trim();

        Node subject = getOrCreateNode(subjectRaw, graph);
        Node object = getOrCreateNode(objectRaw, graph);

        if (!graph.isNodeIdUnique(subject) || !graph.isNodeIdUnique(object)) {
            throw new InvalidConfigurationException(ERROR_MULTIPLE_PRODUCT_IDS);
        }

        if ((relation.equalsIgnoreCase(CONTAINED_IN) && object instanceof Product)
                || (relation.equalsIgnoreCase(CONTAINS) && subject instanceof Product)) {
            throw new InvalidConfigurationException(ERROR_PRODUCTS_CONTAIN);
        }

        if ((relation.equalsIgnoreCase(HAS_PART) || relation.equalsIgnoreCase(PART_OF)
                || relation.equalsIgnoreCase(SUCCESSOR_OF) || relation.equalsIgnoreCase(PREDECESSOR_OF))
                && (object instanceof Category || subject instanceof Category)) {
            throw new InvalidConfigurationException(ERROR_CATEGORIES_PARTS);
        }

        if (Objects.equals(subject.getName(), object.getName())) {
            throw new InvalidConfigurationException(ERROR_SELF_RELATIONSHIP + subjectRaw);
        }

        if (graph.hasEdge(subject, object, relation)) {
            throw new InvalidConfigurationException(ERROR_DUPLICATE_RELATIONSHIP
                    + subjectRaw + ARROW_SIGN + relation + ARROW_SIGN + objectRaw);
        }

        graph.addNode(subject);
        graph.addNode(object);

        graph.addEdge(subject, object, relation);
        graph.addEdge(object, subject, relationType.getReverseRelation());
    }

    /**
     * Normalizes the node name to ensure case insensitivity.
     *
     * @param name Original node name
     * @return Normalized name
     */
    private String normalizeName(String name) {
        return name.toLowerCase();
    }

    private Node getOrCreateNode(String nodeString, Graph graph) {
        Matcher matcher = PRODUCT_PATTERN.matcher(nodeString);

        if (matcher.matches()) {
            String name = matcher.group(1).trim().toLowerCase();
            int id = Integer.parseInt(matcher.group(2).trim());

            Node existingNode = graph.getNode(name);
            return existingNode != null ? existingNode : new Product(name, id);
        }

        String normalizedName = nodeString.toLowerCase();
        Node existingNode = graph.getNode(normalizedName);
        return existingNode != null ? existingNode : new Category(normalizedName);
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
}