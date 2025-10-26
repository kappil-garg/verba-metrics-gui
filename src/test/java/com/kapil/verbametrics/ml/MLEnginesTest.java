package com.kapil.verbametrics.ml;

import com.kapil.verbametrics.ml.services.MLModelService;
import com.kapil.verbametrics.ml.services.ModelTrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class MLEnginesTest {

    @Autowired
    private MLModelService mlModelService;

    @Autowired
    private ModelTrainingService modelTrainingService;

    @Test
    public void testModelTrainingService() {
        assertNotNull(modelTrainingService);
    }

    @Test
    public void testMLModelService() {
        assertNotNull(mlModelService);
        assertNotNull(mlModelService.listModels());
    }

    @Test
    public void testMLConfiguration() {
        assertNotNull(mlModelService);
        assertNotNull(modelTrainingService);
    }

}
