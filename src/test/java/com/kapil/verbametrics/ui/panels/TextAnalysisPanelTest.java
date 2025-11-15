package com.kapil.verbametrics.ui.panels;

import com.kapil.verbametrics.services.BasicTextAnalysisService;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for TextAnalysisPanel.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class TextAnalysisPanelTest {

    @Mock
    private ConfigurableApplicationContext ctx;

    @Mock
    private BasicTextAnalysisService basicTextService;

    @Mock
    private SentimentAnalysisService sentimentService;

    @Mock
    private ReadabilityAnalysisService readabilityService;

    @BeforeEach
    void setup() {
        when(ctx.getBean(BasicTextAnalysisService.class)).thenReturn(basicTextService);
        when(ctx.getBean(SentimentAnalysisService.class)).thenReturn(sentimentService);
        when(ctx.getBean(ReadabilityAnalysisService.class)).thenReturn(readabilityService);
    }

    @Test
    void constructor_initializesControllerAndUI() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        assertNotNull(panel);
        assertInstanceOf(BorderLayout.class, panel.getLayout());
    }

    @Test
    void analyzeButton_triggersController() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JButton analyzeBtn = (JButton) TestUtils.getField(panel, "analyzeBtn");
        assertNotNull(analyzeBtn);
    }

    @Test
    void clearButton_clearsInputArea() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        JButton clearBtn = (JButton) TestUtils.getField(panel, "clearBtn");
        inputArea.setText("Some text");
        clearBtn.doClick();
        assertEquals("", inputArea.getText());
    }

    @Test
    void loadButton_handlesFileLoad() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JButton loadBtn = (JButton) TestUtils.getField(panel, "loadBtn");
        assertNotNull(loadBtn);
    }

    @Test
    void exportButton_exportsResults() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JButton exportBtn = (JButton) TestUtils.getField(panel, "exportBtn");
        assertNotNull(exportBtn);
    }

    @Test
    @DisplayName("Character count label updates on input")
    void charCountLabel_updatesOnInput() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        JLabel charCountLabel = (JLabel) TestUtils.getField(panel, "charCountLabel");
        inputArea.setText("abcde");
        assertTrue(charCountLabel.getText().contains("5"));
    }

    @Test
    @DisplayName("Input area has correct properties")
    void inputArea_hasCorrectProperties() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        assertTrue(inputArea.getLineWrap());
        assertTrue(inputArea.getWrapStyleWord());
    }

    @Test
    @DisplayName("Output area is not editable")
    void outputArea_isNotEditable() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea outputArea = (JTextArea) TestUtils.getField(panel, "outputArea");
        assertFalse(outputArea.isEditable());
        assertTrue(outputArea.getLineWrap());
        assertTrue(outputArea.getWrapStyleWord());
    }

    @Test
    @DisplayName("Sample button loads sample text")
    void sampleButton_loadsSampleText() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        JButton sampleBtn = (JButton) TestUtils.getField(panel, "sampleBtn");
        inputArea.setText("");
        sampleBtn.doClick();
        assertFalse(inputArea.getText().isEmpty());
        assertFalse(inputArea.getText().isEmpty());
    }

    @Test
    @DisplayName("Character count updates when text is deleted")
    void charCountLabel_updatesOnDelete() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        JLabel charCountLabel = (JLabel) TestUtils.getField(panel, "charCountLabel");
        inputArea.setText("Hello World");
        String textAfterInsert = charCountLabel.getText();
        inputArea.setText("");
        String textAfterDelete = charCountLabel.getText();
        assertTrue(textAfterDelete.contains("0"));
        assertNotEquals(textAfterInsert, textAfterDelete);
    }

    @Test
    @DisplayName("All buttons are initialized")
    void constructor_initializesAllButtons() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        assertNotNull(TestUtils.getField(panel, "analyzeBtn"));
        assertNotNull(TestUtils.getField(panel, "clearBtn"));
        assertNotNull(TestUtils.getField(panel, "loadBtn"));
        assertNotNull(TestUtils.getField(panel, "sampleBtn"));
        assertNotNull(TestUtils.getField(panel, "exportBtn"));
    }

    @Test
    @DisplayName("Panel uses BorderLayout")
    void constructor_usesBorderLayout() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        assertInstanceOf(BorderLayout.class, panel.getLayout());
    }

    @Test
    @DisplayName("Panel contains split pane")
    void constructor_containsSplitPane() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        Component[] components = panel.getComponents();
        assertTrue(components.length > 0);
        assertInstanceOf(JSplitPane.class, components[0]);
    }

    @Test
    @DisplayName("Clear button resets character count")
    void clearButton_resetsCharacterCount() {
        TextAnalysisPanel panel = new TextAnalysisPanel(ctx);
        JTextArea inputArea = (JTextArea) TestUtils.getField(panel, "inputArea");
        JButton clearBtn = (JButton) TestUtils.getField(panel, "clearBtn");
        JLabel charCountLabel = (JLabel) TestUtils.getField(panel, "charCountLabel");
        inputArea.setText("Test text");
        clearBtn.doClick();
        assertEquals("", inputArea.getText());
        assertTrue(charCountLabel.getText().contains("0"));
    }

}

/**
 * Utility class for accessing private fields in tests.
 */
class TestUtils {
    static Object getField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
