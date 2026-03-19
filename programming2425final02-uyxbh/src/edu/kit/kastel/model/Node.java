package edu.kit.kastel.model;

/**
 * Base abstract Class representing graph node.
 *
 * @author uyxbh
 */
public abstract class Node {
    protected String name;

    /**
     * Parameterized constructor of Node.
     * @param name Node name
     */
    public Node(String name) {
        this.name = name.toLowerCase(); // Ensures case-insensitive uniqueness
    }

    /**
     * Returns Node name.
     * @return name of Node
     */
    public String getName() {
        return name;
    }
}