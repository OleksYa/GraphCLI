package edu.kit.kastel.model;

/**
 * Class representing graph node of Product type.
 *
 * @author uyxbh
 */
public class Product extends Node {
    private final int id;

    /**
     * Parameterized construct of Product.
     * @param name Product name
     * @param id Product identifier
     */
    public Product(String name, int id) {
        super(name);
        this.id = id;
    }

    /**
     * Return Product identifier.
     * @return identifier of Product
     */
    public int getId() {
        return id;
    }
}
