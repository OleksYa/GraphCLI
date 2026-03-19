package edu.kit.kastel.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Implement Command Output.
 * @author uyxbh
 */
public class CommandOutput {
    private static final String OUTPUT_LINE_SEPARATOR = "\r\n";
    private final List<String> outputLines = new ArrayList<>();
    private CommandStatus status = CommandStatus.CONTINUE;
    private boolean showEmptyOutput = false;

    /**
     * Implement toString().
     * @return all output lines as single string
     */
    @Override
    public String toString() {
        return String.join(OUTPUT_LINE_SEPARATOR, outputLines);
    }

    /**
     * Add lines to output.
     * @param lines collection
     */
    public void addLines(List<String> lines) {
        outputLines.addAll(lines);
    }

    /**
     * Add single line to output.
     * @param line value
     */
    public void add(String line) {
        outputLines.add(line);
    }

    /**
     * Get Output status.
     * @return status of Command Output
     */
    public CommandStatus getStatus() {
        return status;
    }

    /**
     * Set Output status.
     * @param status of Output
     */
    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    /**
     * Get boolean representing whether an empty line should be displayed.
     * @return value of this variable
     */
    public boolean getShowEmptyOutput() {
        return showEmptyOutput;
    }

    /**
     * Set boolean representing whether an empty line should be displayed.
     * @param value input value
     */
    public void setShowEmptyOutput(boolean value) {
        showEmptyOutput = value;
    }
}
