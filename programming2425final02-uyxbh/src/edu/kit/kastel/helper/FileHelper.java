package edu.kit.kastel.helper;

import edu.kit.kastel.exceptions.ReadConfigurationFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A helper class wrapping {@link Files#readAllLines(Path)} for convenient usage.
 *
 * @author uyxbh
 */
public final class FileHelper {
    private static final String INVALID_PATH_ERROR_MESSAGE = "An invalid path has been passed!";

    private FileHelper() {
    }
    /**
     * Returns all lines of a file specified by the given path.
     * @param path the path to the file to read
     * @return all lines of the specified file
     * @throws ReadConfigurationFileException if an invalid path has been passed
     */
    public static List<String> readAllLines(String path) throws ReadConfigurationFileException {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            throw new ReadConfigurationFileException(INVALID_PATH_ERROR_MESSAGE, e);
        }
    }
}
