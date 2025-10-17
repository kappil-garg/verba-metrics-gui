package com.kapil.verbametrics.ui.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for file operations across the GUI application.
 * Provides reusable methods for file loading, saving, and dialog management.
 *
 * @author Kapil Garg
 */
public final class FileOperationsUtil {

    private FileOperationsUtil() {

    }

    /**
     * Load text content from a file.
     *
     * @param path the path to the file
     * @return the file content as a string
     * @throws IOException if an I/O error occurs
     */
    public static String loadTextFile(Path path) throws IOException {
        return Files.readString(path);
    }

    /**
     * Save text content to a file.
     *
     * @param content the content to save
     * @param path    the path where to save the file
     * @throws IOException if an I/O error occurs
     */
    public static void saveTextFile(String content, Path path) throws IOException {
        Files.writeString(path, content);
    }

    /**
     * Show a file open dialog with specified file filters.
     *
     * @param parent     the parent component
     * @param extensions the file extensions to filter (e.g., "txt", "md", "csv")
     * @return the selected file, or null if cancelled
     */
    public static File showOpenDialog(Component parent, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        setupFileFilters(chooser, extensions);
        int result = chooser.showOpenDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    /**
     * Show a file save dialog with specified file filters.
     *
     * @param parent      the parent component
     * @param defaultName the default filename
     * @param extensions  the file extensions to filter (e.g., "txt", "md", "csv")
     * @return the selected file, or null if cancelled
     */
    public static File showSaveDialog(Component parent, String defaultName, String... extensions) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultName));
        setupFileFilters(chooser, extensions);
        int result = chooser.showSaveDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    /**
     * Setup file filters for a file chooser.
     *
     * @param chooser    the file chooser to configure
     * @param extensions the file extensions to filter
     */
    private static void setupFileFilters(JFileChooser chooser, String... extensions) {
        if (extensions.length == 0) {
            return;
        }
        for (String ext : extensions) {
            String description = getFileDescription(ext);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter(description, ext));
        }
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("All Files (*.*)", "*"));
        chooser.setFileFilter(chooser.getChoosableFileFilters()[0]);
    }

    /**
     * Get a user-friendly description for a file extension.
     *
     * @param extension the file extension
     * @return a description for the file type
     */
    private static String getFileDescription(String extension) {
        return switch (extension.toLowerCase()) {
            case "txt" -> "Text Files (*.txt)";
            case "md" -> "Markdown Files (*.md)";
            case "csv" -> "CSV Files (*.csv)";
            case "json" -> "JSON Files (*.json)";
            case "xml" -> "XML Files (*.xml)";
            default -> extension.toUpperCase() + " Files (*." + extension + ")";
        };
    }

}
