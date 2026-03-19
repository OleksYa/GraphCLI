package edu.kit.kastel.helper;

import edu.kit.kastel.model.Product;

import java.util.HashSet;
import java.util.Set;
/**
 * Helps with error handling while working with recommend commands.
 *
 * @author uyxbh
 */
public class ParseRecommendations {
    private final Set<Product> productSet;
    private String validationError;

    /**
     * Parameterless constructor.
     */
    public ParseRecommendations() {
        productSet = new HashSet<>();
        validationError = "";
    }

    /**
     * Get recommended products.
     * @return hashset of recommended products
     */
    public Set<Product> getRecommendedProducts() {
        return productSet;
    }

    /**
     * Get validation error for the recommend command.
     * @return error text as String
     */
    public String getValidationError() {
        return validationError;
    }

    /**
     * Set validation error for recommend command.
     * @param validationError error message
     */
    public void setValidationError(String validationError) {
        this.validationError = validationError;
    }

    /**
     * Shows if recommend command input has errors.
     * @return true if there is an error, false otherwise
     */
    public boolean hasError() {
        return validationError != null && !validationError.isEmpty();
    }
}
