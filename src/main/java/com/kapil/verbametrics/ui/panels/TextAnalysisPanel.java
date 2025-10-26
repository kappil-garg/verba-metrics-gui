package com.kapil.verbametrics.ui.panels;

import com.kapil.verbametrics.domain.ReadabilityMetrics;
import com.kapil.verbametrics.domain.SentimentScore;
import com.kapil.verbametrics.dto.TextAnalysisResponse;
import com.kapil.verbametrics.services.BasicTextAnalysisService;
import com.kapil.verbametrics.services.ReadabilityAnalysisService;
import com.kapil.verbametrics.services.SentimentAnalysisService;
import com.kapil.verbametrics.ui.controller.TextAnalysisController;
import com.kapil.verbametrics.ui.util.FileOperationsUtil;
import com.kapil.verbametrics.ui.util.UIStateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.kapil.verbametrics.ui.util.GuiConstants.*;

/**
 * Panel for text analysis functionality.
 *
 * @author Kapil Garg
 */
public class TextAnalysisPanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextAnalysisPanel.class);

    private final TextAnalysisController controller;

    private final JTextArea inputArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLS);
    private final JTextArea outputArea = new JTextArea(TEXTAREA_ROWS, TEXTAREA_COLS);

    private final JButton analyzeBtn = new JButton(ANALYZE);
    private final JButton clearBtn = new JButton(CLEAR_TEXT);
    private final JButton loadBtn = new JButton(LOAD_FROM_FILE);
    private final JButton sampleBtn = new JButton(LOAD_SAMPLE_TEXT);
    private final JButton exportBtn = new JButton(EXPORT_RESULTS);
    private final JLabel charCountLabel = new JLabel("Characters: 0");

    /**
     * Constructor to initialize the text analysis panel.
     *
     * @param ctx the Spring application context
     */
    public TextAnalysisPanel(ConfigurableApplicationContext ctx) {
        this.controller = new TextAnalysisController(
                ctx.getBean(BasicTextAnalysisService.class),
                ctx.getBean(SentimentAnalysisService.class),
                ctx.getBean(ReadabilityAnalysisService.class)
        );
        buildUi();
    }

    /**
     * Build the UI components and layout.
     */
    private void buildUi() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createInputSection(), createOutputPanel());
        splitPane.setResizeWeight(0.55);
        add(splitPane, BorderLayout.CENTER);
        registerActions();
    }

    /**
     * Create the input section with text area and buttons.
     *
     * @return the input section panel
     */
    private JPanel createInputSection() {
        JPanel inputSection = new JPanel(new BorderLayout());
        inputSection.add(createInputPanel(), BorderLayout.CENTER);
        inputSection.add(createButtonPanel(), BorderLayout.SOUTH);
        return inputSection;
    }

    /**
     * Create the input text panel.
     *
     * @return the input text panel
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Text"));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        return inputPanel;
    }

    /**
     * Create the button panel with action buttons and character count.
     *
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (JButton btn : new JButton[]{analyzeBtn, clearBtn, sampleBtn, loadBtn, exportBtn}) {
            buttonPanel.add(btn);
            buttonPanel.add(Box.createHorizontalStrut(10));
        }
        buttonPanel.remove(buttonPanel.getComponentCount() - 1);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(charCountLabel);
        return buttonPanel;
    }

    /**
     * Create the output panel to display results.
     *
     * @return the output panel
     */
    private JPanel createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return outputPanel;
    }

    /**
     * Register action listeners for buttons and keyboard shortcuts.
     */
    private void registerActions() {
        setupButtonListeners();
        setupKeyboardShortcuts();
        setupDocumentListeners();
    }

    /**
     * Setup button action listeners.
     */
    private void setupButtonListeners() {
        analyzeBtn.addActionListener(e -> onAnalyze());
        clearBtn.addActionListener(e -> onClear());
        sampleBtn.addActionListener(e -> fillSample());
        loadBtn.addActionListener(e -> onLoadFromFile());
        exportBtn.addActionListener(e -> onExportResults());
    }

    /**
     * Setup keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        KeyStroke ctrlEnter = KeyStroke.getKeyStroke("control ENTER");
        inputArea.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlEnter, "analyze");
        inputArea.getActionMap().put("analyze", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onAnalyze();
            }
        });
    }

    /**
     * Setup document listeners for real-time updates (like character count).
     */
    private void setupDocumentListeners() {
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharCount();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharCount();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCharCount();
            }
        });
    }

    /**
     * Handle the analyze button action.
     */
    private void onAnalyze() {
        if (!validateInput()) {
            return;
        }
        setRunningState(true);
        new AnalysisWorker(inputArea.getText()).execute();
    }

    /**
     * Handle the clear button action.
     */
    private void onClear() {
        UIStateUtil.clearTextArea(inputArea);
        UIStateUtil.clearTextArea(outputArea);
        UIStateUtil.requestFocus(inputArea);
    }

    /**
     * Validate the input text area.
     *
     * @return true if valid, false otherwise
     */
    private boolean validateInput() {
        String text = inputArea.getText();
        if (text == null || text.isBlank()) {
            UIStateUtil.showWarning(this, "Please enter text to analyze.", "Validation");
            return false;
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            UIStateUtil.showWarning(this, "Text too long (max " + MAX_TEXT_LENGTH + " characters).", "Validation");
            return false;
        }
        return true;
    }

    /**
     * Set UI state for running or idle analysis.
     *
     * @param running true if analysis is running
     */
    private void setRunningState(boolean running) {
        UIStateUtil.setButtonEnabled(analyzeBtn, !running);
        if (running) {
            UIStateUtil.setTextAreaContent(outputArea, "Running analysis...", -1);
        }
    }

    /**
     * Format the analysis results into a readable string.
     *
     * @param basic       the basic text analysis response
     * @param sentiment   the sentiment score
     * @param readability the readability metrics
     * @return formatted analysis result string
     */
    private String formatAnalysisResult(TextAnalysisResponse basic, SentimentScore sentiment, ReadabilityMetrics readability) {
        return "Basic Stats:\n" +
                "- Words: " + basic.basicStats().wordCount() + "\n" +
                "- Sentences: " + basic.basicStats().sentenceCount() + "\n" +
                "- Characters: " + basic.basicStats().characterCount() + "\n\n" +
                "Sentiment:\n" +
                "- Label: " + sentiment.label() + "\n" +
                "- Confidence: " + String.format("%.3f", sentiment.confidence()) + "\n" +
                "- Score: " + String.format("%.3f", sentiment.score()) + "\n\n" +
                "Readability:\n" +
                "- Flesch-Kincaid Score: " + String.format("%.2f", readability.fleschKincaidScore()) + "\n" +
                "- Reading Ease: " + String.format("%.2f", readability.fleschReadingEase()) + "\n" +
                "- Reading Level: " + readability.readingLevel() + "\n" +
                "- Complexity: " + readability.complexity() + "\n";
    }

    /**
     * Fill the input area with a sample text for analysis.
     */
    private void fillSample() {
        UIStateUtil.setTextAreaContent(inputArea, SAMPLE_TEXT, SAMPLE_TEXT.length());
        UIStateUtil.requestFocus(inputArea);
    }

    /**
     * Handle the load from file button action.
     */
    private void onLoadFromFile() {
        File file = FileOperationsUtil.showOpenDialog(this, "txt", "md", "csv");
        if (file != null) {
            try {
                String content = FileOperationsUtil.loadTextFile(file.toPath());
                UIStateUtil.setTextAreaContent(inputArea, content, -1);
            } catch (Exception ex) {
                LOGGER.error("Failed to load file", ex);
                UIStateUtil.showError(this, "Failed to load file: " + ex.getMessage(), "Failed to load file");
            }
        }
    }

    /**
     * Update the character count display and color based on length.
     */
    private void updateCharCount() {
        int count = inputArea.getText().length();
        String text = "Characters: " + count;
        Color color;
        if (count > MAX_TEXT_LENGTH) {
            color = Color.RED;
        } else if (count > MAX_TEXT_LENGTH * 0.8) {
            color = Color.ORANGE;
        } else {
            color = Color.BLACK;
        }
        UIStateUtil.setLabelText(charCountLabel, text, color);
    }

    /**
     * Handle the export results button action.
     */
    private void onExportResults() {
        if (outputArea.getText().isEmpty()) {
            UIStateUtil.showWarning(this, "No results to export. Please analyze some text first.", "No Results");
            return;
        }
        File file = FileOperationsUtil.showSaveDialog(this, "analysis_results.txt", "txt");
        if (file != null) {
            try {
                FileOperationsUtil.saveTextFile(outputArea.getText(), file.toPath());
                UIStateUtil.showInfo(this, "Results exported successfully!", "Export Successful");
            } catch (IOException ex) {
                LOGGER.error("Failed to export results", ex);
                UIStateUtil.showError(this, "Failed to export results: " + ex.getMessage(), "Export Failed");
            }
        }
    }

    /**
     * SwingWorker for running analysis in the background.
     *
     * @see SwingWorker
     */
    private class AnalysisWorker extends SwingWorker<TextAnalysisController.AnalysisResult, Void> {
        private final String text;
        AnalysisWorker(String text) {
            this.text = text;
        }
        @Override
        protected TextAnalysisController.AnalysisResult doInBackground() {
            return controller.analyze(text);
        }
        @Override
        protected void done() {
            setRunningState(false);
            try {
                var result = get();
                String formattedResult = formatAnalysisResult(result.basic(), result.sentiment(), result.readability());
                UIStateUtil.setTextAreaContent(outputArea, formattedResult, -1);
            } catch (Exception ex) {
                UIStateUtil.clearTextArea(outputArea);
                LOGGER.error("Analysis failed", ex);
                UIStateUtil.showError(TextAnalysisPanel.this, "Analysis failed: " + ex.getMessage(), "Analysis Failed");
            }
        }
    }

}
