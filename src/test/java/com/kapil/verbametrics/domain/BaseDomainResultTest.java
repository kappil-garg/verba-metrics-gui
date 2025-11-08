package com.kapil.verbametrics.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BaseDomainResult validation and utility methods.
 *
 * @author Kapil Garg
 */
class BaseDomainResultTest {

    @Test
    @DisplayName("validateStringField accepts valid non-blank string")
    void validateStringField_validString() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateStringField("valid", "Test field")
        );
    }

    @Test
    @DisplayName("validateStringField rejects null")
    void validateStringField_nullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateStringField(null, "Test field")
        );
        assertEquals("Test field cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateStringField rejects blank string")
    void validateStringField_blankValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateStringField("   ", "Test field")
        );
        assertEquals("Test field cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateStringField rejects empty string")
    void validateStringField_emptyValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateStringField("", "Test field")
        );
        assertEquals("Test field cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("validateNonNegative(int) accepts zero")
    void validateNonNegativeInt_zero() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateNonNegative(0, "Test field")
        );
    }

    @Test
    @DisplayName("validateNonNegative(int) accepts positive value")
    void validateNonNegativeInt_positive() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateNonNegative(42, "Test field")
        );
    }

    @Test
    @DisplayName("validateNonNegative(int) rejects negative value")
    void validateNonNegativeInt_negative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateNonNegative(-1, "Test field")
        );
        assertEquals("Test field cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("validateNonNegative(double) accepts zero")
    void validateNonNegativeDouble_zero() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateNonNegative(0.0, "Test field")
        );
    }

    @Test
    @DisplayName("validateNonNegative(double) accepts positive value")
    void validateNonNegativeDouble_positive() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateNonNegative(42.5, "Test field")
        );
    }

    @Test
    @DisplayName("validateNonNegative(double) rejects negative value")
    void validateNonNegativeDouble_negative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateNonNegative(-0.1, "Test field")
        );
        assertEquals("Test field cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("validateRange(double) accepts value within range")
    void validateRangeDouble_withinRange() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateRange(5.0, 0.0, 10.0, "Test field")
        );
    }

    @Test
    @DisplayName("validateRange(double) accepts minimum boundary")
    void validateRangeDouble_minBoundary() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateRange(0.0, 0.0, 10.0, "Test field")
        );
    }

    @Test
    @DisplayName("validateRange(double) accepts maximum boundary")
    void validateRangeDouble_maxBoundary() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateRange(10.0, 0.0, 10.0, "Test field")
        );
    }

    @Test
    @DisplayName("validateRange(double) rejects value below minimum")
    void validateRangeDouble_belowMin() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateRange(-0.1, 0.0, 10.0, "Test field")
        );
        assertEquals("Test field must be between 0.0 and 10.0", exception.getMessage());
    }

    @Test
    @DisplayName("validateRange(double) rejects value above maximum")
    void validateRangeDouble_aboveMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateRange(10.1, 0.0, 10.0, "Test field")
        );
        assertEquals("Test field must be between 0.0 and 10.0", exception.getMessage());
    }

    @Test
    @DisplayName("validateRange(int) accepts value within range")
    void validateRangeInt_withinRange() {
        assertDoesNotThrow(() ->
                BaseDomainResult.validateRange(5, 0, 10, "Test field")
        );
    }

    @Test
    @DisplayName("validateRange(int) rejects value below minimum")
    void validateRangeInt_belowMin() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateRange(-1, 0, 10, "Test field")
        );
        assertEquals("Test field must be between 0 and 10", exception.getMessage());
    }

    @Test
    @DisplayName("validateRange(int) rejects value above maximum")
    void validateRangeInt_aboveMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                BaseDomainResult.validateRange(11, 0, 10, "Test field")
        );
        assertEquals("Test field must be between 0 and 10", exception.getMessage());
    }

    @Test
    @DisplayName("safeDivide(double) returns correct result")
    void safeDivideDouble_validDenominator() {
        assertEquals(2.5, BaseDomainResult.safeDivide(5.0, 2.0), 0.001);
    }

    @Test
    @DisplayName("safeDivide(double) returns zero for zero denominator")
    void safeDivideDouble_zeroDenominator() {
        assertEquals(0.0, BaseDomainResult.safeDivide(5.0, 0.0), 0.001);
    }

    @Test
    @DisplayName("safeDivide(int) returns correct result")
    void safeDivideInt_validDenominator() {
        assertEquals(2.5, BaseDomainResult.safeDivide(5, 2), 0.001);
    }

    @Test
    @DisplayName("safeDivide(int) returns zero for zero denominator")
    void safeDivideInt_zeroDenominator() {
        assertEquals(0.0, BaseDomainResult.safeDivide(5, 0), 0.001);
    }

    @Test
    @DisplayName("getConfidenceLevel returns HIGH for high confidence")
    void getConfidenceLevel_high() {
        TestDomainResult result = new TestDomainResult();
        assertEquals("HIGH", result.getConfidenceLevel(0.85));
    }

    @Test
    @DisplayName("getConfidenceLevel returns MEDIUM for medium confidence")
    void getConfidenceLevel_medium() {
        TestDomainResult result = new TestDomainResult();
        assertEquals("MEDIUM", result.getConfidenceLevel(0.65));
    }

    @Test
    @DisplayName("getConfidenceLevel returns LOW for low confidence")
    void getConfidenceLevel_low() {
        TestDomainResult result = new TestDomainResult();
        assertEquals("LOW", result.getConfidenceLevel(0.45));
    }

    @Test
    @DisplayName("getConfidenceLevel with custom thresholds")
    void getConfidenceLevel_customThresholds() {
        TestDomainResult result = new TestDomainResult();
        assertEquals("HIGH", result.getConfidenceLevel(0.9, 0.8, 0.5));
        assertEquals("MEDIUM", result.getConfidenceLevel(0.6, 0.8, 0.5));
        assertEquals("LOW", result.getConfidenceLevel(0.3, 0.8, 0.5));
    }

    /**
     * Concrete implementation of BaseDomainResult for testing protected instance methods.
     */
    private static class TestDomainResult extends BaseDomainResult {
        // Empty implementation to test non-static protected methods like getConfidenceLevel()
    }

}
