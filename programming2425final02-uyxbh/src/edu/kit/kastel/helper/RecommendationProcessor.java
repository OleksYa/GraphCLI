package edu.kit.kastel.helper;

import edu.kit.kastel.model.Edge;
import edu.kit.kastel.model.Node;
import edu.kit.kastel.model.Product;
import edu.kit.kastel.model.Category;
import edu.kit.kastel.model.Graph;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes product recommendations based on strategies S1, S2, and S3.
 * Implements a recursive descent parser for UNION and INTERSECTION.
 *
 * @author uyxbh
 */
public class RecommendationProcessor {
    private static final String ERROR_INVALID_INTERSECTION = "Error, invalid intersection params";
    private static final String ERROR_INVALID_COMMAND_PARAMS = "Error, invalid recommendation command params";
    private static final String ERROR_INVALID_PRODUCT_ID = "Error, invalid product id";
    private static final String ERROR_NO_PRODUCT = "Error, no product with id: %s exists";
    private static final String ERROR_INVALID_STRATEGY = "Error, invalid strategy: %s";
    private static final String CONTAINED_IN = "contained-in";
    private static final String CONTAINS = "contains";
    private static final String PREDECESSOR_OF = "predecessor-of";
    private static final String SUCCESSOR_OF = "successor-of";
    private static final String UNION_REGEX = "(?i)UNION\\s*\\(.*\\)";
    private static final String INTERSECTION_REGEX = "(?i)INTERSECTION\\s*\\(.*\\)";
    private static final char OPEN_PAREN = '(';
    private static final char CLOSE_PAREN = ')';
    private static final char COMMA = ',';
    private static final String SPACE = "\\s+";
    private static final String STRATEGY_1 = "S1";
    private static final String STRATEGY_2 = "S2";
    private static final String STRATEGY_3 = "S3";
    private static final int RECURSION_START_DEPTH = 0;
    private static final int ITERATION_STEP = 1;


    private final Graph graph;

    /**
     * Parameterized constructor for Recommendation Processor.
     * @param graph Products and Categories graph
     */
    public RecommendationProcessor(Graph graph) {
        this.graph = graph;
    }

    /**
     * Parses the recommendation command and returns recommended products.
     * @param input The term to be parsed (e.g., "S1 105" or "UNION(S1 105, S3 107)")
     * @return A sorted set of recommended products
     */
    public ParseRecommendations parseAndRecommend(String input) {
        return parseTerm(input.trim());
    }

    private ParseRecommendations parseTerm(String input) {
        String inputTerm = input.trim();

        if (inputTerm.matches(UNION_REGEX)) {
            return parseUnion(inputTerm.substring(inputTerm.indexOf(OPEN_PAREN) + 1, inputTerm.lastIndexOf(CLOSE_PAREN)).trim());
        } else if (inputTerm.matches(INTERSECTION_REGEX)) {
            return parseIntersection(inputTerm.substring(inputTerm.indexOf(OPEN_PAREN) + 1, inputTerm.lastIndexOf(CLOSE_PAREN)).trim());
        } else {
            return parseFinal(inputTerm);
        }
    }

    private ParseRecommendations parseUnion(String input) {
        String[] parts = splitTerms(input);
        ParseRecommendations result = new ParseRecommendations();
        for (String part : parts) {
            ParseRecommendations parseRecommendations = parseTerm(part.trim());
            if (parseRecommendations.hasError()) {
                return parseRecommendations;
            }
            result.getRecommendedProducts().addAll(parseRecommendations.getRecommendedProducts());
        }
        return result;
    }

    private ParseRecommendations parseIntersection(String input) {
        String[] parts = splitTerms(input);
        if (parts.length < 2) {
            ParseRecommendations parseRecommendations = new ParseRecommendations();
            parseRecommendations.setValidationError(ERROR_INVALID_INTERSECTION);
            return parseRecommendations;
        }

        ParseRecommendations result = parseTerm(parts[0].trim());
        for (int i = 1; i < parts.length; i++) {
            ParseRecommendations parseRecommendations = parseTerm(parts[i].trim());
            if (parseRecommendations.hasError()) {
                return parseRecommendations;
            }
            result.getRecommendedProducts().retainAll(parseRecommendations.getRecommendedProducts());
        }
        return result;
    }

    private ParseRecommendations parseFinal(String input) {
        String[] parts = input.trim().split(SPACE);
        if (parts.length != 2) {
            ParseRecommendations parseRecommendations = new ParseRecommendations();
            parseRecommendations.setValidationError(ERROR_INVALID_COMMAND_PARAMS);
            return parseRecommendations;
        }

        String strategy = parts[0];
        int productId;
        try {
            productId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            ParseRecommendations parseRecommendations = new ParseRecommendations();
            parseRecommendations.setValidationError(ERROR_INVALID_PRODUCT_ID);
            return parseRecommendations;
        }

        Product product = findProductById(productId);
        if (product == null) {
            ParseRecommendations parseRecommendations = new ParseRecommendations();
            parseRecommendations.setValidationError(ERROR_NO_PRODUCT.formatted(productId));
            return parseRecommendations;
        }

        switch (strategy) {
            case STRATEGY_1:
                return recommendS1(product);
            case STRATEGY_2:
                return recommendS2(product);
            case STRATEGY_3:
                return recommendS3(product);
            default:
                ParseRecommendations parseRecommendations = new ParseRecommendations();
                parseRecommendations.setValidationError(ERROR_INVALID_STRATEGY.formatted(strategy));
                return parseRecommendations;
        }
    }

    private Product findProductById(int productId) {
        for (Node node : graph.getNodes()) {
            if (node instanceof Product product) {
                if (product.getId() == productId) {
                    return product;
                }
            }
        }
        return null;
    }

    private ParseRecommendations recommendS1(Product product) {
        Set<Product> recommendations = new HashSet<>();
        Set<Category> parentCategories = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().getName().equalsIgnoreCase(product.getName())
                    && edge.getTarget() instanceof Category category
                    && edge.getRelation().equalsIgnoreCase(CONTAINED_IN)) {
                parentCategories.add(category);
            }
        }

        for (Category category : parentCategories) {
            for (Edge edge : graph.getEdges()) {
                if (edge.getRelation().equals(CONTAINS)
                        && edge.getSource().getName().equalsIgnoreCase(category.getName())
                        && edge.getTarget() instanceof Product product2
                        && !product2.equals(product)) {
                    recommendations.add(product2);
                }
            }
        }

        ParseRecommendations parseRecommendations = new ParseRecommendations();
        parseRecommendations.getRecommendedProducts().addAll(recommendations);
        return parseRecommendations;
    }

    private ParseRecommendations recommendS2(Product product) {
        Set<Product> recommendations = new HashSet<>();
        findSuccessorsByPredecessors(product, recommendations);
        recommendations.remove(product);

        ParseRecommendations parseRecommendations = new ParseRecommendations();
        parseRecommendations.getRecommendedProducts().addAll(recommendations);
        return parseRecommendations;
    }

    private void findSuccessorsByPredecessors(Product product, Set<Product> recommendations) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().getName().equalsIgnoreCase(product.getName())
                    && edge.getRelation().equals(PREDECESSOR_OF)
                    && edge.getTarget() instanceof Product product2) {
                if (!recommendations.contains(product2) && !product2.equals(product)) {
                    recommendations.add(product2);
                    findSuccessorsByPredecessors(product2, recommendations);
                }
            }
        }
    }

    private ParseRecommendations recommendS3(Product product) {
        Set<Product> recommendations = new HashSet<>();
        findPredecessorsBySuccessors(product, recommendations);
        recommendations.remove(product);

        ParseRecommendations parseRecommendations = new ParseRecommendations();
        parseRecommendations.getRecommendedProducts().addAll(recommendations);
        return parseRecommendations;
    }

    private void findPredecessorsBySuccessors(Product product, Set<Product> recommendations) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().getName().equalsIgnoreCase(product.getName())
                    && edge.getRelation().equals(SUCCESSOR_OF)
                    && edge.getTarget() instanceof Product product2) {
                if (!recommendations.contains(product2) && !product2.equals(product)) {
                    recommendations.add(product2);
                    findPredecessorsBySuccessors(product2, recommendations);
                }
            }
        }
    }

    private String[] splitTerms(String input) {
        List<String> terms = new ArrayList<>();
        int level = RECURSION_START_DEPTH;
        int start = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == OPEN_PAREN) {
                level++;
            } else if (c == CLOSE_PAREN) {
                level--;
            } else if (c == COMMA && level == RECURSION_START_DEPTH) {
                terms.add(input.substring(start, i).trim());
                start = i + ITERATION_STEP;
            }
        }

        terms.add(input.substring(start).trim());
        return terms.toArray(new String[0]);
    }
}