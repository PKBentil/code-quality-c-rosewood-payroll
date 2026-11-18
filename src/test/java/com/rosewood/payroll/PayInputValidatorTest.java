package com.rosewood.payroll;

import com.rosewood.payroll.exceptions.InvalidEmployeeIdException;
import com.rosewood.payroll.exceptions.InvalidHoursException;
import com.rosewood.payroll.exceptions.InvalidRateException;
import com.rosewood.payroll.validation.PayInputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PayInputValidatorTest {

    private final PayInputValidator validator = new PayInputValidator();

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.5, 40.0, 41.0, 100.0, 168.0})
    void acceptsZeroAndPositiveFiniteHoursUpToTheWeeklyLimit(double hours) {
        assertDoesNotThrow(() -> validator.validateHours(hours));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -1.0, -40.0})
    void rejectsNegativeHours(double hours) {
        InvalidHoursException ex =
                assertThrows(InvalidHoursException.class, () -> validator.validateHours(hours));
        assertEquals(hours, ex.getHours());
        assertTrue(ex.getMessage().contains("must not be negative"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejectsNonFiniteHours(double hours) {
        InvalidHoursException ex =
                assertThrows(InvalidHoursException.class, () -> validator.validateHours(hours));
        assertEquals(hours, ex.getHours());
        assertTrue(ex.getMessage().contains("must be a finite number"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {168.01, 1000.0, 1.0E9})
    void rejectsHoursAboveTheWeeklyLimit(double hours) {
        InvalidHoursException ex =
                assertThrows(InvalidHoursException.class, () -> validator.validateHours(hours));
        assertEquals(hours, ex.getHours());
        assertTrue(ex.getMessage().contains("must not exceed"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "20", "999.99", "10000"})
    void acceptsZeroAndPositiveRatesUpToTheLimit(String rate) {
        assertDoesNotThrow(() -> validator.validateRate(new BigDecimal(rate)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-20"})
    void rejectsNegativeRates(String rate) {
        BigDecimal value = new BigDecimal(rate);
        InvalidRateException ex =
                assertThrows(InvalidRateException.class, () -> validator.validateRate(value));
        assertEquals(value, ex.getRate());
        assertTrue(ex.getMessage().contains("must not be negative"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"10000.01", "1000000"})
    void rejectsRatesAboveTheLimit(String rate) {
        BigDecimal value = new BigDecimal(rate);
        InvalidRateException ex =
                assertThrows(InvalidRateException.class, () -> validator.validateRate(value));
        assertEquals(value, ex.getRate());
        assertTrue(ex.getMessage().contains("must not exceed"));
    }

    @Test
    void rejectsMissingRate() {
        InvalidRateException ex =
                assertThrows(InvalidRateException.class, () -> validator.validateRate(null));
        assertNull(ex.getRate());
        assertTrue(ex.getMessage().contains("must be provided"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void rejectsABlankEmployeeId(String employeeId) {
        InvalidEmployeeIdException ex =
                assertThrows(InvalidEmployeeIdException.class, () -> validator.validateEmployeeId(employeeId));
        assertEquals(employeeId, ex.getEmployeeId());
    }

    @Test
    void acceptsAnEmployeeIdWithContent() {
        assertDoesNotThrow(() -> validator.validateEmployeeId("e1"));
    }
}
