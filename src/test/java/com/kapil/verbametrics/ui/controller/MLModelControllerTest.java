package com.kapil.verbametrics.ui.controller;

import com.kapil.verbametrics.ml.domain.MLModel;
import com.kapil.verbametrics.ml.services.MLModelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for MLModelController.
 *
 * @author Kapil Garg
 */
class MLModelControllerTest {

    @Test
    @DisplayName("getAllModels delegates to service")
    void getAllModels_delegates() {
        MLModelService service = Mockito.mock(MLModelService.class);
        MLModelController controller = new MLModelController(service);
        when(service.listModels()).thenReturn(List.of());
        assertNotNull(controller.getAllModels());
    }

    @Test
    @DisplayName("getModel delegates and returns service value")
    void getModel_delegates() {
        MLModelService service = Mockito.mock(MLModelService.class);
        MLModelController controller = new MLModelController(service);
        MLModel model = new MLModel("id", "SENTIMENT", "n", "d", "v", LocalDateTime.now(), LocalDateTime.now(), Map.of(), Map.of(), "p", true, "u", 1, 0.9, "TRAINED");
        when(service.getModel("id")).thenReturn(model);
        assertEquals(model, controller.getModel("id"));
    }

    @Test
    @DisplayName("deleteModel delegates and returns service result")
    void deleteModel_delegates() {
        MLModelService service = Mockito.mock(MLModelService.class);
        MLModelController controller = new MLModelController(service);
        when(service.deleteModel("id")).thenReturn(true);
        assertTrue(controller.deleteModel("id"));
    }

}
