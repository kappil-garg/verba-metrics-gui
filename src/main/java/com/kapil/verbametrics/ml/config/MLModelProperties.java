package com.kapil.verbametrics.ml.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Configuration for machine learning model properties.
 * Includes settings for training limits, default parameters, supported algorithms, and more.
 *
 * @author Kapil Garg
 */
@Component
@ConfigurationProperties(prefix = "ml.model")
@Data
public class MLModelProperties {

    private Map<String, Integer> trainingLimits;
    private Map<String, Object> defaultParameters;
    private Map<String, String> supportedAlgorithms;

    private List<String> supportedModelTypes;

    private Map<String, String> fileSettings;
    private Map<String, Integer> cacheSettings;
    private Map<String, List<String>> requiredFields;
    private Map<String, Double> performanceThresholds;

    public MLModelProperties() {
        this.trainingLimits = Map.of();
        this.defaultParameters = Map.of();
        this.supportedAlgorithms = Map.of();
        this.supportedModelTypes = List.of();
        this.fileSettings = Map.of();
        this.cacheSettings = Map.of();
        this.requiredFields = Map.of();
        this.performanceThresholds = Map.of();
    }

}
