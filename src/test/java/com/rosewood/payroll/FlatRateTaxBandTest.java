package com.rosewood.payroll;

import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.TaxBand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FlatRateTaxBandTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);

    }

    private final TaxBand medium = FlatRateTaxBand.between(bd("1000"), bd("5000"), bd("0.15"));
    private final TaxBand openEnded = FlatRateTaxBand.from(bd("5000"), bd("0.25"));

    @ParameterizedTest(name = "{0} in [1000, 5000) is {1}")
    @CsvSource({
            "999.99,  false",
            "1000,    true",   // lower bound is included
            "1000.01, true",
            "4999.99, true",
            "5000,    false"   // upper bound is excluded
    })
    void boundedBandIncludesTheLowerBoundAndExcludesTheUpperBound(String gross, boolean expected) {
        assertEquals(expected, medium.appliesTo(bd(gross)));
    }

    @ParameterizedTest(name = "{0} in [5000, no limit) is {1}")
    @CsvSource({
            "4999.99, false",
            "5000,    true",
            "1000000, true"
    })
    void openEndedBandHasNoUpperLimit(String gross, boolean expected) {
        assertEquals(expected, openEnded.appliesTo(bd(gross)));
    }

    @ParameterizedTest(name = "15% of {0} = {1}")
    @CsvSource({
            "1650.00, 247.50",
            "1000.00, 150.00",
            "4999.99, 750.00"   // 749.9985 rounds half-up to 750.00
    })
    void taxIsTheRateTimesGrossRoundedHalfUp(String gross, String expectedTax) {
        assertEquals(bd(expectedTax), medium.taxOn(bd(gross)));
    }

    @Test
    void acceptsRatesOfZeroAndOne() {
        assertDoesNotThrow(() -> FlatRateTaxBand.from(bd("0"), bd("0")));
        assertDoesNotThrow(() -> FlatRateTaxBand.from(bd("0"), bd("1")));
    }

    @Test
    void rejectsARateBelowZeroOrAboveOne() {
        assertThrows(IllegalArgumentException.class, () -> FlatRateTaxBand.from(bd("0"), bd("-0.01")));
        assertThrows(IllegalArgumentException.class, () -> FlatRateTaxBand.from(bd("0"), bd("1.01")));
    }

    @Test
    void rejectsAnUpperBoundAtOrBelowTheLowerBound() {
        assertThrows(IllegalArgumentException.class,
                () -> FlatRateTaxBand.between(bd("1000"), bd("1000"), bd("0.15")));
        assertThrows(IllegalArgumentException.class,
                () -> FlatRateTaxBand.between(bd("1000"), bd("999"), bd("0.15")));
    }

    @Test
    void rejectsMissingValues() {
        assertThrows(NullPointerException.class, () -> FlatRateTaxBand.from(null, bd("0.15")));
        assertThrows(NullPointerException.class, () -> FlatRateTaxBand.from(bd("0"), null));
        assertThrows(NullPointerException.class, () -> FlatRateTaxBand.between(bd("0"), null, bd("0.15")));
    }
}