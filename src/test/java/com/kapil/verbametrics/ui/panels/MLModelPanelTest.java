package com.kapil.verbametrics.ui.panels;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.ml.services.ModelCleanupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for MLModelPanel.
 *
 * @author Kapil Garg
 */
@ExtendWith(MockitoExtension.class)
class MLModelPanelTest {

    @Mock
    private ConfigurableApplicationContext ctx;

    @Mock
    private MLModelService mlModelService;

    @Mock
    private ModelCleanupService cleanupService;

    @BeforeEach
    void setUp() {
        when(ctx.getBean(MLModelService.class)).thenReturn(mlModelService);
        when(ctx.getBean(ModelCleanupService.class)).thenReturn(cleanupService);
        when(mlModelService.listModels()).thenReturn(List.of());
    }

    @Test
    @DisplayName("MLModelPanel initializes with correct layout")
    void constructor_initializesWithCorrectLayout() {
        MLModelPanel panel = new MLModelPanel(ctx);
        assertNotNull(panel);
        assertInstanceOf(java.awt.BorderLayout.class, panel.getLayout());
    }

    @Test
    @DisplayName("MLModelPanel contains tabbed pane with all tabs")
    void constructor_containsAllTabs() {
        MLModelPanel panel = new MLModelPanel(ctx);
        Component[] components = panel.getComponents();
        assertTrue(components.length >= 1);
        JTabbedPane tabbedPane = MLModelPanelTestHelper.findTabbedPane(panel);
        assertNotNull(tabbedPane);
        assertEquals(4, tabbedPane.getTabCount());
        assertEquals("Model Management", tabbedPane.getTitleAt(0));
        assertEquals("Train Model", tabbedPane.getTitleAt(1));
        assertEquals("Evaluate Model", tabbedPane.getTitleAt(2));
        assertEquals("Make Prediction", tabbedPane.getTitleAt(3));
    }

    @Test
    @DisplayName("Model table is initialized")
    void constructor_initializesModelTable() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTable modelTable = (JTable) UITestUtils.getField(panel, "modelTable");
        assertNotNull(modelTable);
        assertFalse(modelTable.isCellEditable(0, 0));
    }

    @Test
    @DisplayName("Refresh button exists and is functional")
    void refreshButton_existsAndFunctional() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton refreshBtn = (JButton) UITestUtils.getField(panel, "refreshBtn");
        assertNotNull(refreshBtn);
        assertEquals("Refresh Models", refreshBtn.getText());
        when(mlModelService.listModels()).thenReturn(List.of());
        refreshBtn.doClick();
        verify(mlModelService, atLeastOnce()).listModels();
    }

    @Test
    @DisplayName("Delete button exists")
    void deleteButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton deleteBtn = (JButton) UITestUtils.getField(panel, "deleteBtn");
        assertNotNull(deleteBtn);
        assertEquals("Delete Model", deleteBtn.getText());
    }

    @Test
    @DisplayName("View details button exists")
    void viewDetailsButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton viewDetailsBtn = (JButton) UITestUtils.getField(panel, "viewDetailsBtn");
        assertNotNull(viewDetailsBtn);
        assertEquals("View Details", viewDetailsBtn.getText());
    }

    @Test
    @DisplayName("Train button exists")
    void trainButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton trainBtn = (JButton) UITestUtils.getField(panel, "trainBtn");
        assertNotNull(trainBtn);
        assertEquals("Train Model", trainBtn.getText());
    }

    @Test
    @DisplayName("Training data area exists")
    void trainingDataArea_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea trainingDataArea = (JTextArea) UITestUtils.getField(panel, "trainingDataArea");
        assertNotNull(trainingDataArea);
    }

    @Test
    @DisplayName("Clear training data button clears the area")
    void clearTrainingDataButton_clearsArea() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea trainingDataArea = (JTextArea) UITestUtils.getField(panel, "trainingDataArea");
        JButton clearBtn = (JButton) UITestUtils.getField(panel, "clearTrainingDataBtn");
        trainingDataArea.setText("Some training data");
        clearBtn.doClick();
        assertEquals("", trainingDataArea.getText());
    }

    @Test
    @DisplayName("Model name field exists")
    void modelNameField_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextField modelNameField = (JTextField) UITestUtils.getField(panel, "modelNameField");
        assertNotNull(modelNameField);
    }

    @Test
    @DisplayName("Model description area exists")
    void modelDescriptionArea_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea modelDescriptionArea = (JTextArea) UITestUtils.getField(panel, "modelDescriptionArea");
        assertNotNull(modelDescriptionArea);
    }

    @Test
    @DisplayName("Test data area exists")
    void testDataArea_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea testDataArea = (JTextArea) UITestUtils.getField(panel, "testDataArea");
        assertNotNull(testDataArea);
    }

    @Test
    @DisplayName("Clear test data button clears the area")
    void clearTestDataButton_clearsArea() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea testDataArea = (JTextArea) UITestUtils.getField(panel, "testDataArea");
        JButton clearBtn = (JButton) UITestUtils.getField(panel, "clearTestDataBtn");
        testDataArea.setText("Some test data");
        clearBtn.doClick();
        assertEquals("", testDataArea.getText());
    }

    @Test
    @DisplayName("Prediction input area exists")
    void predictionInputArea_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea predictionInputArea = (JTextArea) UITestUtils.getField(panel, "predictionInputArea");
        assertNotNull(predictionInputArea);
    }

    @Test
    @DisplayName("Clear prediction input button clears the area")
    void clearPredictionInputButton_clearsArea() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea predictionInputArea = (JTextArea) UITestUtils.getField(panel, "predictionInputArea");
        JButton clearBtn = (JButton) UITestUtils.getField(panel, "clearPredictionInputBtn");
        predictionInputArea.setText("Some prediction data");
        clearBtn.doClick();
        assertEquals("", predictionInputArea.getText());
    }

    @Test
    @DisplayName("Results area exists and is not editable")
    void resultsArea_existsAndNotEditable() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JTextArea resultsArea = (JTextArea) UITestUtils.getField(panel, "resultsArea");
        assertNotNull(resultsArea);
        assertFalse(resultsArea.isEditable());
    }

    @Test
    @DisplayName("Evaluate button exists")
    void evaluateButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton evaluateBtn = (JButton) UITestUtils.getField(panel, "evaluateBtn");
        assertNotNull(evaluateBtn);
        assertEquals("Evaluate Model", evaluateBtn.getText());
    }

    @Test
    @DisplayName("Predict button exists")
    void predictButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton predictBtn = (JButton) UITestUtils.getField(panel, "predictBtn");
        assertNotNull(predictBtn);
        assertEquals("Make Prediction", predictBtn.getText());
    }

    @Test
    @DisplayName("Panel loads models on initialization")
    void constructor_loadsModels() {
        MLModel model = new MLModel("model-1", "SENTIMENT", "Test Model", "Description", "1.0",
                LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(), "/models/model-1",
                true, "system", 100, 0.85, "TRAINED"
        );
        when(mlModelService.listModels()).thenReturn(List.of(model));
        MLModelPanel panel = new MLModelPanel(ctx);
        verify(mlModelService).listModels();
        JTable modelTable = (JTable) UITestUtils.getField(panel, "modelTable");
        assertEquals(1, modelTable.getRowCount());
    }

    @Test
    @DisplayName("Load training data button exists")
    void loadTrainingDataButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton loadBtn = (JButton) UITestUtils.getField(panel, "loadTrainingDataBtn");
        assertNotNull(loadBtn);
        assertEquals("Load from File", loadBtn.getText());
    }

    @Test
    @DisplayName("Load test data button exists")
    void loadTestDataButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton loadBtn = (JButton) UITestUtils.getField(panel, "loadTestDataBtn");
        assertNotNull(loadBtn);
        assertEquals("Load from File", loadBtn.getText());
    }

    @Test
    @DisplayName("Load prediction data button exists")
    void loadPredictionDataButton_exists() {
        MLModelPanel panel = new MLModelPanel(ctx);
        JButton loadBtn = (JButton) UITestUtils.getField(panel, "loadPredictionDataBtn");
        assertNotNull(loadBtn);
        assertEquals("Load from File", loadBtn.getText());
    }

}

/**
 * Utility class for accessing private fields in UI tests.
 */
class UITestUtils {
    static Object getField(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
    }
}

/**
 * Helper class for MLModelPanelTest.
 */
class MLModelPanelTestHelper {
    static JTabbedPane findTabbedPane(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTabbedPane) {
                return (JTabbedPane) comp;
            }
            if (comp instanceof Container) {
                JTabbedPane found = findTabbedPane((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
