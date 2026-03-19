package edu.kit.kastel.model;

/**
 * Class representing Edge of the graph.
 *
 * @author uyxbh
 */
public class Edge {
    private Node source;
    private Node target;
    private String relation;

    /**
     * Parameterized constructor for the graph Edge.
     * @param source Source node
     * @param target Target node
     * @param relation Edge relation type
     */
    public Edge(Node source, Node target, String relation) {
        this.source = source;
        this.target = target;
        this.relation = relation;
    }

    /**
     * Get Edge source Node.
     * @return source Node
     */
    public Node getSource() {
        return source;
    }

    /**
     * Get Target Node.
     * @return target Node
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Relation type as string.
     * @return string representation of Relation
     */
    public String getRelation() {
        return relation;
    }
}