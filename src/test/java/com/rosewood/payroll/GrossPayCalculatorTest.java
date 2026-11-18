package com.rosewood.payroll;

import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrossPayCalculatorTest {

    private final GrossPayCalculator calculator = new GrossPayCalculator();

    @ParameterizedTest(name = "{0}h at {1} gives gross {2}")
    @CsvSource({
            "0,     20,    0.00",      // a valid zero
            "40,    20,    800.00",    // exactly 40 hours: no overtime
            "40.5,  20,    815.00",    // 800 + 0.5 * 20 * 1.5
            "41,    10.01, 415.42",    // 400.40 + 15.015 = 415.415, rounded half-up to 415.42
            "40.1,  20,    803.00",    // 800 + 0.1 * 20 * 1.5, with no binary rounding noise
            "50,    30,    1650.00"    // 1200 + 10 * 30 * 1.5
    })
    void grossIsRegularPayPlusOvertimeAtOneAndAHalf(double hours, String rate, String expectedGross) {
        assertEquals(new BigDecimal(expectedGross), calculator.calculate(hours, new BigDecimal(rate)));
    }
}
