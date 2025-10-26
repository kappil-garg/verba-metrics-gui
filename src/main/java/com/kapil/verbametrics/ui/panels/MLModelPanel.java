package com.kapil.verbametrics.ui.panels;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.domain.ModelEvaluationResult;
import com.kapil.verbametrics.ml.domain.ModelTrainingResult;
import com.kapil.verbametrics.ml.services.ModelCleanupService;
import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.ui.controller.MLModelController;
import com.kapil.verbametrics.ui.util.FileOperationsUtil;
import com.kapil.verbametrics.ui.util.UIStateUtil;
import com.kapil.verbametrics.util.JsonParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Panel for ML model management functionality.
 * Provides interface for training, evaluating, and managing ML models.
 *
 * @author Kapil Garg
 */
public class MLModelPanel extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MLModelPanel.class);

    private final MLModelController controller;
    private final ModelCleanupService cleanupService;

    // Model Management Components
    private final JTable modelTable = new JTable();
    private final JButton refreshBtn = new JButton("Refresh Models");
    private final JButton deleteBtn = new JButton("Delete Model");
    private final JButton viewDetailsBtn = new JButton("View Details");

    // Model Selection Components (shared across tabs)
    private final JComboBox<String> evaluationModelCombo = new JComboBox<>();
    private final JComboBox<String> predictionModelCombo = new JComboBox<>();

    // Training Components
    private final JComboBox<String> modelTypeCombo = new JComboBox<>(new String[]{"SENTIMENT", "CLASSIFICATION", "TOPIC_MODELING"});
    private final JButton trainBtn = new JButton("Train Model");
    private final JButton loadTrainingDataBtn = new JButton("Load from File");
    private final JButton clearTrainingDataBtn = new JButton("Clear");
    private final JTextArea trainingDataArea = new JTextArea(8, 50);
    private final JTextField modelNameField = new JTextField(20);
    private final JTextArea modelDescriptionArea = new JTextArea(3, 30);

    // Evaluation Components
    private final JButton evaluateBtn = new JButton("Evaluate Model");
    private final JButton loadTestDataBtn = new JButton("Load from File");
    private final JButton clearTestDataBtn = new JButton("Clear");
    private final JTextArea testDataArea = new JTextArea(8, 50);

    // Prediction Components
    private final JButton predictBtn = new JButton("Make Prediction");
    private final JButton loadPredictionDataBtn = new JButton("Load from File");
    private final JButton clearPredictionInputBtn = new JButton("Clear");
    private final JTextArea predictionInputArea = new JTextArea(8, 50);

    // Results Area
    private final JTextArea resultsArea = new JTextArea(10, 60);

    /**
     * Constructor to initialize the ML model panel.
     *
     * @param ctx the Spring application context
     */
    public MLModelPanel(ConfigurableApplicationContext ctx) {
        this.controller = new MLModelController(ctx.getBean(MLModelService.class));
        this.cleanupService = ctx.getBean(ModelCleanupService.class);
        buildUi();
        loadModels();
    }

    /**
     * Build the UI components and layout.
     */
    private void buildUi() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Model Management", createModelManagementPanel());
        tabbedPane.addTab("Train Model", createTrainingPanel());
        tabbedPane.addTab("Evaluate Model", createEvaluationPanel());
        tabbedPane.addTab("Make Prediction", createPredictionPanel());
        tabbedPane.addChangeListener(e -> clearResults());
        add(tabbedPane, BorderLayout.CENTER);
        JPanel resultsPanel = createResultsPanel();
        add(resultsPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the model management panel.
     *
     * @return the model management panel
     */
    private JPanel createModelManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        setupModelTable();
        JScrollPane tableScroll = new JScrollPane(modelTable);
        panel.add(tableScroll, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        refreshBtn.addActionListener(e -> refreshModels());
        viewDetailsBtn.addActionListener(e -> viewModelDetails());
        deleteBtn.addActionListener(e -> deleteSelectedModel());
        return panel;
    }

    /**
     * Create the training panel.
     *
     * @return the training panel
     */
    private JPanel createTrainingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Model Type:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(modelTypeCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Model Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(modelNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(modelDescriptionArea), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Training Data (JSON):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.add(new JScrollPane(trainingDataArea), BorderLayout.CENTER);
        JPanel dataButtonPanel = new JPanel(new FlowLayout());
        dataButtonPanel.add(loadTrainingDataBtn);
        dataButtonPanel.add(clearTrainingDataBtn);
        dataPanel.add(dataButtonPanel, BorderLayout.SOUTH);
        formPanel.add(dataPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(trainBtn, gbc);
        panel.add(formPanel, BorderLayout.CENTER);
        loadTrainingDataBtn.addActionListener(e -> loadTrainingData());
        clearTrainingDataBtn.addActionListener(e -> clearTrainingData());
        trainBtn.addActionListener(e -> trainModel());
        return panel;
    }

    /**
     * Create the evaluation panel.
     *
     * @return the evaluation panel
     */
    private JPanel createEvaluationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Select Model:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(evaluationModelCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Test Data (JSON):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.add(new JScrollPane(testDataArea), BorderLayout.CENTER);
        JPanel dataButtonPanel = new JPanel(new FlowLayout());
        dataButtonPanel.add(loadTestDataBtn);
        dataButtonPanel.add(clearTestDataBtn);
        dataPanel.add(dataButtonPanel, BorderLayout.SOUTH);
        formPanel.add(dataPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(evaluateBtn, gbc);
        panel.add(formPanel, BorderLayout.CENTER);
        loadTestDataBtn.addActionListener(e -> loadTestData());
        clearTestDataBtn.addActionListener(e -> clearTestData());
        evaluateBtn.addActionListener(e -> evaluateModel());
        return panel;
    }

    /**
     * Create the prediction panel.
     *
     * @return the prediction panel
     */
    private JPanel createPredictionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Select Model:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(predictionModelCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Input Data (JSON):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JScrollPane(predictionInputArea), BorderLayout.CENTER);
        JPanel inputButtonPanel = new JPanel(new FlowLayout());
        inputButtonPanel.add(loadPredictionDataBtn);
        inputButtonPanel.add(clearPredictionInputBtn);
        inputPanel.add(inputButtonPanel, BorderLayout.SOUTH);
        formPanel.add(inputPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(predictBtn, gbc);
        panel.add(formPanel, BorderLayout.CENTER);
        loadPredictionDataBtn.addActionListener(e -> loadPredictionData());
        clearPredictionInputBtn.addActionListener(e -> clearPredictionInput());
        predictBtn.addActionListener(e -> makePrediction());
        return panel;
    }

    /**
     * Create the results panel.
     *
     * @return the results panel
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Results"));
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Set up the model table.
     */
    private void setupModelTable() {
        String[] columns = {"Model ID", "Name", "Type", "Status", "Accuracy", "Created"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelTable.setModel(model);
        modelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Load models into the table and populate all model dropdowns.
     */
    private void loadModels() {
        try {
            List<MLModel> models = controller.getAllModels();
            DefaultTableModel tableModel = (DefaultTableModel) modelTable.getModel();
            tableModel.setRowCount(0);
            for (MLModel model : models) {
                Object[] row = {
                        model.modelId(),
                        model.name(),
                        model.modelType(),
                        model.status(),
                        String.format("%.3f", model.accuracy()),
                        model.createdAt().toString()
                };
                tableModel.addRow(row);
            }
            populateModelDropdowns(models);
            appendResult("Loaded " + models.size() + " models");
        } catch (Exception e) {
            LOGGER.error("Failed to load models", e);
            UIStateUtil.showError(this, "Failed to load models: " + e.getMessage(), "Error");
        }
    }

    /**
     * Populate all model selection dropdowns with available models.
     */
    private void populateModelDropdowns(List<MLModel> models) {
        evaluationModelCombo.removeAllItems();
        predictionModelCombo.removeAllItems();
        for (MLModel model : models) {
            String displayText = model.name() + " (" + model.modelType() + ")";
            evaluationModelCombo.addItem(displayText);
            predictionModelCombo.addItem(displayText);
        }
        if (!models.isEmpty()) {
            evaluationModelCombo.setSelectedIndex(0);
            predictionModelCombo.setSelectedIndex(0);
        }
    }

    /**
     * Refresh models with cleared results panel.
     */
    private void refreshModels() {
        clearResults();
        loadModels();
    }

    /**
     * View details of selected model.
     */
    private void viewModelDetails() {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow == -1) {
            UIStateUtil.showWarning(this, "Please select a model", "No Selection");
            return;
        }
        clearResults();
        try {
            String modelId = (String) modelTable.getValueAt(selectedRow, 0);
            String details = getDetailString(modelId);
            appendResult(details);
        } catch (Exception e) {
            LOGGER.error("Failed to get model details", e);
            UIStateUtil.showError(this, "Failed to get model details: " + e.getMessage(), "Error");
        }
    }

    /**
     * Get detailed string representation of a model.
     *
     * @param modelId the model ID
     * @return detailed string
     */
    private String getDetailString(String modelId) {
        MLModel model = controller.getModel(modelId);
        return "Model Details:\n" +
                "ID: " + model.modelId() + "\n" +
                "Name: " + model.name() + "\n" +
                "Type: " + model.modelType() + "\n" +
                "Status: " + model.status() + "\n" +
                "Accuracy: " + String.format("%.3f", model.accuracy()) + "\n" +
                "Description: " + model.description() + "\n" +
                "Created: " + model.createdAt() + "\n" +
                "Last Used: " + model.lastUsed() + "\n";
    }

    /**
     * Delete selected model.
     */
    private void deleteSelectedModel() {
        int selectedRow = modelTable.getSelectedRow();
        if (selectedRow == -1) {
            UIStateUtil.showWarning(this, "Please select a model", "No Selection");
            return;
        }
        String modelId = (String) modelTable.getValueAt(selectedRow, 0);
        String modelName = (String) modelTable.getValueAt(selectedRow, 1);
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete model '" + modelName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            clearResults();
            try {
                boolean deleted = controller.deleteModel(modelId);
                if (deleted) {
                    // Also delete the corresponding model file
                    boolean fileDeleted = cleanupService.cleanupModelFile(modelId);
                    if (fileDeleted) {
                        LOGGER.debug("Model file deleted successfully for model: {}", modelId);
                    } else {
                        LOGGER.debug("No model file found to delete for model: {}", modelId);
                    }
                    appendResult("Model '" + modelName + "' deleted successfully");
                    loadModels();
                } else {
                    UIStateUtil.showError(this, "Failed to delete model", "Error");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to delete model", e);
                UIStateUtil.showError(this, "Failed to delete model: " + e.getMessage(), "Error");
            }
        }
    }

    /**
     * Load training data from file.
     */
    private void loadTrainingData() {
        File file = FileOperationsUtil.showOpenDialog(this, "json", "csv", "txt");
        if (file != null) {
            try {
                String content = FileOperationsUtil.loadTextFile(file.toPath());
                trainingDataArea.setText(content);
                appendResult("Training data loaded from: " + file.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to load training data", e);
                UIStateUtil.showError(this, "Failed to load training data: " + e.getMessage(), "Error");
            }
        }
    }

    /**
     * Train a new model.
     */
    private void trainModel() {
        if (modelNameField.getText().trim().isEmpty()) {
            UIStateUtil.showWarning(this, "Please enter a model name", "Validation");
            return;
        }
        if (trainingDataArea.getText().trim().isEmpty()) {
            UIStateUtil.showWarning(this, "Please provide training data", "Validation");
            return;
        }
        clearResults();
        try {
            String modelType = (String) modelTypeCombo.getSelectedItem();
            String modelName = modelNameField.getText().trim();
            String description = modelDescriptionArea.getText().trim();
            String trainingDataJson = trainingDataArea.getText().trim();
            appendResult("Training model: " + modelName + " (Type: " + modelType + ")");
            ModelTrainingResult result = controller.trainModel(modelType, modelName, description, trainingDataJson);
            appendResult("Model training completed!");
            appendResult("Model ID: " + result.modelId());
            appendResult("Accuracy: " + String.format("%.3f", result.accuracy()));
            appendResult("F1 Score: " + String.format("%.3f", result.f1Score()));
            modelNameField.setText("");
            modelDescriptionArea.setText("");
            trainingDataArea.setText("");
            loadModels();
        } catch (Exception e) {
            LOGGER.error("Failed to train model", e);
            UIStateUtil.showError(this, "Failed to train model: " + e.getMessage(), "Error");
        }
    }

    /**
     * Load test data from file.
     */
    private void loadTestData() {
        File file = FileOperationsUtil.showOpenDialog(this, "json", "csv", "txt");
        if (file != null) {
            try {
                String content = FileOperationsUtil.loadTextFile(file.toPath());
                testDataArea.setText(content);
                appendResult("Test data loaded from: " + file.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to load test data", e);
                UIStateUtil.showError(this, "Failed to load test data: " + e.getMessage(), "Error");
            }
        }
    }

    /**
     * Load prediction data from file.
     */
    private void loadPredictionData() {
        File file = FileOperationsUtil.showOpenDialog(this, "json", "csv", "txt");
        if (file != null) {
            try {
                String content = FileOperationsUtil.loadTextFile(file.toPath());
                predictionInputArea.setText(content);
                appendResult("Prediction data loaded from: " + file.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to load prediction data", e);
                UIStateUtil.showError(this, "Failed to load prediction data: " + e.getMessage(), "Error");
            }
        }
    }

    /**
     * Evaluate a model.
     */
    private void evaluateModel() {
        clearResults();
        try {
            String selectedModel = (String) evaluationModelCombo.getSelectedItem();
            if (selectedModel == null || selectedModel.isEmpty()) {
                UIStateUtil.showError(this, "Please select a model to evaluate", "No Model Selected");
                return;
            }
            String testDataJson = testDataArea.getText().trim();
            if (testDataJson.isEmpty()) {
                UIStateUtil.showError(this, "Please provide test data for evaluation", "No Test Data");
                return;
            }
            String modelId = selectedModel.substring(0, selectedModel.lastIndexOf(" ("));
            List<MLModel> models = controller.getAllModels();
            MLModel targetModel = models.stream()
                    .filter(model -> model.name().equals(modelId))
                    .findFirst()
                    .orElse(null);
            if (targetModel == null) {
                UIStateUtil.showError(this, "Selected model not found", "Model Not Found");
                return;
            }
            List<Map<String, Object>> testData = JsonParserUtil.parseTrainingData(testDataJson);
            ModelEvaluationResult result = controller.evaluateModel(targetModel.modelId(), testData);
            appendResult("=== Model Evaluation Results ===");
            appendResult("Model: " + targetModel.name() + " (" + targetModel.modelType() + ")");
            appendResult("Test Data Size: " + testData.size() + " samples");
            appendResult("Accuracy: " + String.format("%.3f", result.accuracy()));
            appendResult("Precision: " + String.format("%.3f", result.precision()));
            appendResult("Recall: " + String.format("%.3f", result.recall()));
            appendResult("F1-Score: " + String.format("%.3f", result.f1Score()));
            appendResult("Evaluation completed successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate model", e);
            UIStateUtil.showError(this, "Model evaluation failed: " + e.getMessage(), "Evaluation Error");
        }
    }

    /**
     * Make a prediction.
     */
    private void makePrediction() {
        clearResults();
        try {
            String selectedModel = (String) predictionModelCombo.getSelectedItem();
            if (selectedModel == null || selectedModel.isEmpty()) {
                UIStateUtil.showError(this, "Please select a model for prediction", "No Model Selected");
                return;
            }
            String inputDataJson = predictionInputArea.getText().trim();
            if (inputDataJson.isEmpty()) {
                UIStateUtil.showError(this, "Please provide input data for prediction", "No Input Data");
                return;
            }
            String modelId = selectedModel.substring(0, selectedModel.lastIndexOf(" ("));
            List<MLModel> models = controller.getAllModels();
            MLModel targetModel = models.stream()
                    .filter(model -> model.name().equals(modelId))
                    .findFirst()
                    .orElse(null);
            if (targetModel == null) {
                UIStateUtil.showError(this, "Selected model not found", "Model Not Found");
                return;
            }
            List<Map<String, Object>> inputDataList;
            try {
                inputDataList = JsonParserUtil.parsePredictionData(inputDataJson);
            } catch (Exception e) {
                UIStateUtil.showError(this, "Failed to parse input data: " + e.getMessage(), "Parse Error");
                return;
            }
            appendResult("=== Prediction Results ===");
            appendResult("Model: " + targetModel.name() + " (" + targetModel.modelType() + ")");
            appendResult("Input Data Size: " + inputDataList.size() + " samples");
            appendResult("");
            for (int i = 0; i < inputDataList.size(); i++) {
                Map<String, Object> input = inputDataList.get(i);
                Map<String, Object> prediction = controller.predict(targetModel.modelId(), input);

                appendResult("Sample " + (i + 1) + ":");
                appendResult("  Input: " + input.get("text"));

                if (prediction.containsKey("error") && (Boolean) prediction.get("error")) {
                    appendResult("  Error: " + prediction.get("message"));
                } else {
                    appendResult("  Prediction: " + prediction.get("prediction"));
                    if (prediction.containsKey("confidence")) {
                        appendResult("  Confidence: " + String.format("%.3f", (Double) prediction.get("confidence")));
                    }
                    if (prediction.containsKey("probability")) {
                        appendResult("  Probability: " + String.format("%.3f", (Double) prediction.get("probability")));
                    }
                }
                appendResult("");
            }
            appendResult("Prediction completed successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to make prediction", e);
            UIStateUtil.showError(this, "Prediction failed: " + e.getMessage(), "Prediction Error");
        }
    }

    /**
     * Append result to the results area.
     *
     * @param result the result string to append
     */
    private void appendResult(String result) {
        SwingUtilities.invokeLater(() -> {
            resultsArea.append(result + "\n");
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
        });
    }

    /**
     * Clear the results area.
     */
    private void clearResults() {
        SwingUtilities.invokeLater(() -> resultsArea.setText(""));
    }

    /**
     * Clear training data area.
     */
    private void clearTrainingData() {
        trainingDataArea.setText("");
        appendResult("Training data cleared");
    }

    /**
     * Clear test data area.
     */
    private void clearTestData() {
        testDataArea.setText("");
        clearResults();
    }

    /**
     * Clear prediction input area.
     */
    private void clearPredictionInput() {
        predictionInputArea.setText("");
        clearResults();
    }

}
