package com.rosewood.payroll;

import com.rosewood.payroll.domain.model.Money;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTest {

    @ParameterizedTest(name = "{0} rounds to {1}")
    @CsvSource({
            "49.9995,  50.00",
            "749.9985, 750.00",
            "0.005,    0.01",
            "0.015,    0.02",
            "0.004,    0.00",
            "2,        2.00",
            "-0.005,   -0.01"
    })
    void roundsToTwoDecimalsHalfUp(String input, String expected) {
        assertEquals(new BigDecimal(expected), Money.round(new BigDecimal(input)));
    }
}
