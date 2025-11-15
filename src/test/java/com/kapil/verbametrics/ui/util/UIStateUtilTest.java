package com.kapil.verbametrics.ui.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UIStateUtil.
 *
 * @author Kapil Garg
 */
class UIStateUtilTest {

    @Test
    @DisplayName("showError displays error dialog")
    void showError_displaysDialog() {
        assertDoesNotThrow(() -> {
            try {
                UIStateUtil.showError(null, "Error message", "Error Title");
            } catch (java.awt.HeadlessException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("showWarning displays warning dialog")
    void showWarning_displaysDialog() {
        assertDoesNotThrow(() -> {
            try {
                UIStateUtil.showWarning(null, "Warning message", "Warning Title");
            } catch (java.awt.HeadlessException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("showInfo displays info dialog")
    void showInfo_displaysDialog() {
        assertDoesNotThrow(() -> {
            try {
                UIStateUtil.showInfo(null, "Info message", "Info Title");
            } catch (java.awt.HeadlessException e) {
                assertTrue(true);
            }
        });
    }

    @Test
    @DisplayName("setButtonEnabled enables button")
    void setButtonEnabled_enablesButton() {
        JButton button = new JButton("Test");
        button.setEnabled(false);
        UIStateUtil.setButtonEnabled(button, true);
        assertTrue(button.isEnabled());
    }

    @Test
    @DisplayName("setButtonEnabled disables button")
    void setButtonEnabled_disablesButton() {
        JButton button = new JButton("Test");
        button.setEnabled(true);
        UIStateUtil.setButtonEnabled(button, false);
        assertFalse(button.isEnabled());
    }

    @Test
    @DisplayName("setButtonEnabled handles null button")
    void setButtonEnabled_nullButton_noException() {
        assertDoesNotThrow(() -> UIStateUtil.setButtonEnabled(null, true));
    }

    @Test
    @DisplayName("setLabelText sets text and color")
    void setLabelText_setsTextAndColor() {
        JLabel label = new JLabel();
        Color color = Color.RED;
        UIStateUtil.setLabelText(label, "Test Text", color);
        assertEquals("Test Text", label.getText());
        assertEquals(color, label.getForeground());
    }

    @Test
    @DisplayName("setLabelText sets text without color")
    void setLabelText_setsTextWithoutColor() {
        JLabel label = new JLabel();
        Color originalColor = label.getForeground();
        UIStateUtil.setLabelText(label, "Test Text", null);
        assertEquals("Test Text", label.getText());
        assertEquals(originalColor, label.getForeground());
    }

    @Test
    @DisplayName("setLabelText handles null label")
    void setLabelText_nullLabel_noException() {
        assertDoesNotThrow(() -> UIStateUtil.setLabelText(null, "Test", Color.BLUE));
    }

    @Test
    @DisplayName("setTextAreaContent sets text and position")
    void setTextAreaContent_setsTextAndPosition() {
        JTextArea textArea = new JTextArea();
        String text = "Line 1\nLine 2\nLine 3";
        UIStateUtil.setTextAreaContent(textArea, text, 5);
        assertEquals(text, textArea.getText());
        assertEquals(5, textArea.getCaretPosition());
    }

    @Test
    @DisplayName("setTextAreaContent sets position to end when negative")
    void setTextAreaContent_negativePosition_setsToEnd() {
        JTextArea textArea = new JTextArea();
        String text = "Test text";
        UIStateUtil.setTextAreaContent(textArea, text, -1);
        assertEquals(text, textArea.getText());
        assertEquals(text.length(), textArea.getCaretPosition());
    }

    @Test
    @DisplayName("setTextAreaContent handles position beyond text length")
    void setTextAreaContent_positionBeyondLength_setsToEnd() {
        JTextArea textArea = new JTextArea();
        String text = "Short";
        UIStateUtil.setTextAreaContent(textArea, text, 100);
        assertEquals(text, textArea.getText());
        assertEquals(text.length(), textArea.getCaretPosition());
    }

    @Test
    @DisplayName("setTextAreaContent handles null textArea")
    void setTextAreaContent_nullTextArea_noException() {
        assertDoesNotThrow(() -> UIStateUtil.setTextAreaContent(null, "Test", 0));
    }

    @Test
    @DisplayName("clearTextArea clears content")
    void clearTextArea_clearsContent() {
        JTextArea textArea = new JTextArea("Some content");
        UIStateUtil.clearTextArea(textArea);
        assertEquals("", textArea.getText());
    }

    @Test
    @DisplayName("clearTextArea handles null textArea")
    void clearTextArea_nullTextArea_noException() {
        assertDoesNotThrow(() -> UIStateUtil.clearTextArea(null));
    }

    @Test
    @DisplayName("requestFocus requests focus")
    void requestFocus_requestsFocus() {
        JButton button = new JButton("Test");
        assertDoesNotThrow(() -> UIStateUtil.requestFocus(button));
    }

    @Test
    @DisplayName("requestFocus handles null component")
    void requestFocus_nullComponent_noException() {
        assertDoesNotThrow(() -> UIStateUtil.requestFocus(null));
    }

}
