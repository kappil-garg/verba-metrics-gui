package com.kapil.verbametrics.ui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for UI state management across the GUI application.
 * Provides reusable methods for common operations like showing dialogs, managing button states, and handling feedback.
 *
 * @author Kapil Garg
 */
public final class UIStateUtil {

    private UIStateUtil() {

    }

    /**
     * Show an error message dialog.
     *
     * @param parent  the parent component
     * @param message the error message
     * @param title   the dialog title
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a warning message dialog.
     *
     * @param parent  the parent component
     * @param message the warning message
     * @param title   the dialog title
     */
    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show an information message dialog.
     *
     * @param parent  the parent component
     * @param message the information message
     * @param title   the dialog title
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Enable or disable a button.
     *
     * @param button  the button to modify
     * @param enabled true to enable, false to disable
     */
    public static void setButtonEnabled(JButton button, boolean enabled) {
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    /**
     * Set the text of a label with optional color coding.
     *
     * @param label the label to update
     * @param text  the text to set
     * @param color the color to use (null for default)
     */
    public static void setLabelText(JLabel label, String text, Color color) {
        if (label != null) {
            label.setText(text);
            if (color != null) {
                label.setForeground(color);
            }
        }
    }

    /**
     * Set the text of a text area and position the cursor.
     *
     * @param textArea the text area to update
     * @param text     the text to set
     * @param position the cursor position (-1 for end of text)
     */
    public static void setTextAreaContent(JTextArea textArea, String text, int position) {
        if (textArea != null) {
            textArea.setText(text);
            if (position >= 0) {
                textArea.setCaretPosition(Math.min(position, text.length()));
            } else {
                textArea.setCaretPosition(text.length());
            }
        }
    }

    /**
     * Clear the content of a text area.
     *
     * @param textArea the text area to clear
     */
    public static void clearTextArea(JTextArea textArea) {
        if (textArea != null) {
            textArea.setText("");
        }
    }

    /**
     * Request focus for a component.
     *
     * @param component the component to focus
     */
    public static void requestFocus(Component component) {
        if (component != null) {
            component.requestFocusInWindow();
        }
    }

}
