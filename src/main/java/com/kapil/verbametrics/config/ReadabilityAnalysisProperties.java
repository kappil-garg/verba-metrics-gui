package com.kapil.verbametrics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for readability analysis.
 * Contains coefficients for Flesch-Kincaid and Flesch Reading Ease for reading and complexity levels.
 *
 * @author Kapil Garg
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "readability.analysis")
public class ReadabilityAnalysisProperties {

    /**
     * Reading level thresholds
     */
    private ReadingLevels readingLevels = new ReadingLevels();

    /**
     * Flesch-Kincaid formula coefficients
     */
    private FleschKincaid fleschKincaid = new FleschKincaid();

    /**
     * Complexity level thresholds
     */
    private ComplexityLevels complexityLevels = new ComplexityLevels();

    /**
     * Flesch Reading Ease formula coefficients
     */
    private FleschReadingEase fleschReadingEase = new FleschReadingEase();

    @Data
    public static class ReadingLevels {
        private double elementary = 6.0;
        private double middleSchool = 9.0;
        private double highSchool = 12.0;
        private double college = 16.0;
    }

    @Data
    public static class FleschKincaid {
        private double sentenceLengthMultiplier = 0.39;
        private double syllablesPerWordMultiplier = 11.8;
        private double constant = -15.59;
    }

    @Data
    public static class ComplexityLevels {
        private double veryEasy = 80.0;
        private double easy = 60.0;
        private double moderate = 40.0;
        private double difficult = 20.0;
    }

    @Data
    public static class FleschReadingEase {
        private double constant = 206.835;
        private double sentenceLengthMultiplier = 1.015;
        private double syllablesPerWordMultiplier = 84.6;
    }

}
