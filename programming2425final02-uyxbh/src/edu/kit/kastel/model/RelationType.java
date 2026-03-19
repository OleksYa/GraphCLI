package edu.kit.kastel.model;

/**
 * Enum representing valid relationship types in the graph.
 * Ensures only predefined relationships are allowed.
 *
 * @author uyxbh
 */
public enum RelationType {
    /**
     * Represent Contains type of Relation.
     */
    CONTAINS("contains", "contained-in"),
    /**
     * Represent Contained-In type of Relation.
     */
    CONTAINED_IN("contained-in", "contains"),
    /**
     * Represent Part-Of type of Relation.
     */
    PART_OF("part-of", "has-part"),
    /**
     * Represent Has Part type of Relation.
     */
    HAS_PART("has-part", "part-of"),
    /**
     * Represent Successor Of type of Relation.
     */
    SUCCESSOR_OF("successor-of", "predecessor-of"),
    /**
     * Represent Predecessor type of Relation.
     */
    PREDECESSOR_OF("predecessor-of", "successor-of");

    private final String relation;
    private final String reverseRelation;

    RelationType(String relation, String reverseRelation) {
        this.relation = relation;
        this.reverseRelation = reverseRelation;
    }

    /**
     * Return type of Relation.
     * @return type name of Relation
     */
    public String getRelation() {
        return relation;
    }

    /**
     *  Return reversed type of Relation.
     * @return reversed type name of Relation
     */
    public String getReverseRelation() {
        return reverseRelation;
    }

    /**
     * Returns the corresponding RelationType for a given string, or null if invalid.
     * @param relation type name of Relation
     * @return RelationType enum element
     */
    public static RelationType fromString(String relation) {
        for (RelationType type : values()) {
            if (type.relation.equalsIgnoreCase(relation)) {
                return type;
            }
        }
        return null;
    }
}