package edu.kit.kastel.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;

/**
 * Class representing Products and Categories graph.
 *
 * @author uyxbh
 */
public class Graph {
    private static final String PRODUCT_PATTERN = "^(.*?)\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\)$";
    private final Map<String, Node> nodes = new HashMap<>();  // Stores nodes by name (case-insensitive)
    private final Map<Integer, Node> idToNodeMap = new HashMap<>();  // New map to store nodes by ID
    private final List<Edge> edges = new ArrayList<>();

    /**
     * Add a node to the graph.
     * @param node Node of graph
     */
    public void addNode(Node node) {
        nodes.put(node.getName().toLowerCase(), node); // Store by name (case-insensitive)
        if (node instanceof Product product) {
            idToNodeMap.put(product.getId(), product); // Store by ID for products
        }
    }

    /**
     * Add an edge to the graph.
     * @param source Source node of Edge
     * @param target Target node of Edge
     * @param relation Type of added relation
     */
    public void addEdge(Node source, Node target, String relation) {
        edges.add(new Edge(source, target, relation));
    }

    /**
     * Get a node by name and ID (for products).
     * @param nodeString string definition of the node
     * @return Node onject created from the text description
     */
    public Node getNode(String nodeString) {
        // Try to extract name and ID using regex (e.g., "CentOS5 (id=105)")
        Matcher matcher = Pattern.compile(PRODUCT_PATTERN).matcher(nodeString);

        if (matcher.matches()) {
            String name = matcher.group(1).trim().toLowerCase();  // Extract the name
            int id = Integer.parseInt(matcher.group(2).trim());   // Extract the ID

            // First, try to get the node by ID from the map
            Node nodeById = idToNodeMap.get(id);
            if (nodeById != null) {
                return nodeById; // If we found it by ID, return that node
            }

            // If no node by ID, try to get it by name
            Node nodeByName = nodes.get(name);
            if (nodeByName != null) {
                return nodeByName; // If found by name, return the node
            }
        }

        return nodes.get(nodeString.trim().toLowerCase());
    }


    /**
     * Get all edges in the graph.
     * @return List of the graph edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Check if an edge exists in the graph.
     * @param source Source Node
     * @param target Target Node
     * @param relation Edge relation type
     * @return flag indicates if Node exists in graph
     */
    public boolean hasEdge(Node source, Node target, String relation) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source)
                    && edge.getTarget().equals(target)
                    && edge.getRelation().equals(relation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove an edge from the graph.
     * @param source Source node
     * @param target Target node
     * @param relation edge relation type
     * @return flag indicates if Edge is removed
     */
    public boolean removeEdge(Node source, Node target, String relation) {
        return edges.removeIf(edge ->
                edge.getSource().equals(source)
                        && edge.getTarget().equals(target)
                        && edge.getRelation().equals(relation));
    }

    /**
     * Get all nodes in the graph.
     * @return List of graph nodes
     */
    public List<Node> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    /**
     * Replace the current graph with a new graph.
     * @param newGraph New graph instance
     */
    public void replaceWith(Graph newGraph) {
        this.nodes.clear();
        this.edges.clear();
        for (Node node : newGraph.getNodes()) {
            this.addNode(node);
        }
        for (Edge edge : newGraph.getEdges()) {
            this.addEdge(edge.getSource(), edge.getTarget(), edge.getRelation());
        }
    }

    /**
     * Check if graph Node is isolated.
     * @param node Graph Node
     * @return flag indicated if Node is isolated
     */
    public boolean isNodeIsolated(Node node) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) || edge.getTarget().equals(node)) {
                return false; // Node is connected to at least one edge
            }
        }
        return true; // No edges found for the node
    }
    /**
     * Checks if the given node (either a category or a product) has a unique ID in the graph.
     * If the node is a category, the method automatically returns true as categories do not have
     * unique IDs. If the node is a product, it ensures that no other product in the graph has the
     * same ID.
     *
     * @param node The node whose ID needs to be checked for uniqueness. It can either be a product or a category.
     * @return true if the node is a category (categories do not require unique IDs), or if the product's ID is unique in the graph.
     * @throws NullPointerException if the provided node is null.
     */
    public boolean isNodeIdUnique(Node node) {
        if (node instanceof Product product) {
            for (Node graphNode : nodes.values()) {
                if (graphNode instanceof Product && ((Product) graphNode).getId() == product.getId()
                        && !Objects.equals(graphNode.getName(), node.getName())) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }


}