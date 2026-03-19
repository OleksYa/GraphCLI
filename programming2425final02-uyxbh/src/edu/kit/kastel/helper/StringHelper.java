package edu.kit.kastel.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * String helper methods.
 *
 * @author uyxbh
 */
public final class StringHelper {
    private StringHelper() {
    }

    /**
     * Converts source string list to list with lower-case values.
     * @param source Source string list
     * @return List with elements converted to lower case
     */
    public static List<String> toLowerCase(List<String> source) {
        List<String> result = new ArrayList<>();
        for (String sourceValue : source) {
            result.add(sourceValue.toLowerCase());
        }
        return result;
    }
}