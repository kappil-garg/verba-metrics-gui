package com.kapil.verbametrics.ui.util;

/**
 * Constants used across the GUI application.
 *
 * @author Kapil Garg
 */
public final class GuiConstants {

    private GuiConstants() {

    }

    // UI Dimensions
    public static final int TEXTAREA_ROWS = 16;
    public static final int TEXTAREA_COLS = 60;
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final int MIN_WINDOW_WIDTH = 800;
    public static final int MIN_WINDOW_HEIGHT = 600;

    // Button Labels
    public static final String ANALYZE = "Analyze";
    public static final String CLEAR_TEXT = "Clear Text";
    public static final String LOAD_FROM_FILE = "Load from File";
    public static final String LOAD_SAMPLE_TEXT = "Load Sample Text";
    public static final String EXPORT_RESULTS = "Export Results";

    // Window Properties
    public static final String WINDOW_TITLE = "Verba Metrics GUI";

    // Validation Limits
    public static final int MAX_TEXT_LENGTH = 10000;

    // Sample Text
    public static final String SAMPLE_TEXT = "VerbaMetrics is an advanced text analysis tool designed to provide deep insights into textual data. It leverages state-of-the-art natural language processing techniques to analyze sentiment, readability, and key themes within the text. Whether you're a researcher, marketer, or developer, VerbaMetrics offers a comprehensive suite of features to help you understand and utilize text data effectively.";

}
