package com.rosewood.payroll;


import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxBand;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.exceptions.NoApplicableTaxBandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaxCalculatorTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private final TaxCalculator standard = new TaxCalculator(StandardTaxBands.all());

    // Gross and tax figures worked out by hand from the rules: 5% below 1000, 15% below 5000, else 25%.
    @ParameterizedTest(name = "tax on {0} = {1}")
    @CsvSource({
            "0.00,    0.00",     // G11 a valid zero is taxed at the low rate: still zero
            "800.00,  40.00",    // G1
            "830.00,  41.50",    // G3
            "815.00,  40.75",    // G4
            "875.00,  43.75",    // G2
            "999.99,  50.00",    // G6 just below the first threshold; 49.9995 rounds up
            "1000.00, 150.00",   // G7 exactly on the threshold goes to the next band
            "1650.00, 247.50",   // G5
            "4999.99, 750.00",   // G8 just below the second threshold; 749.9985 rounds up
            "5000.00, 1250.00",  // G9 exactly on the threshold goes to the top band
            "7000.00, 1750.00"   // G10
    })
    void taxesEachGrossPayAtTheRightBand(String gross, String expectedTax) {
        assertEquals(bd(expectedTax), standard.taxFor(bd(gross)));
    }

    @Test
    void failsLoudlyWhenNoBandCoversTheAmount() {
        TaxCalculator narrow = new TaxCalculator(List.of(FlatRateTaxBand.between(bd("0"), bd("100"), bd("0.10"))));

        NoApplicableTaxBandException ex =
                assertThrows(NoApplicableTaxBandException.class, () -> narrow.taxFor(bd("100.00")));
        assertEquals(bd("100.00"), ex.getGrossPay());
    }

    @Test
    void firstMatchingBandWins() {
        TaxBand tenPercent = FlatRateTaxBand.from(bd("0"), bd("0.10"));
        TaxBand fiftyPercent = FlatRateTaxBand.from(bd("0"), bd("0.50"));

        assertEquals(bd("10.00"), new TaxCalculator(List.of(tenPercent, fiftyPercent)).taxFor(bd("100")));
        assertEquals(bd("50.00"), new TaxCalculator(List.of(fiftyPercent, tenPercent)).taxFor(bd("100")));
    }

    @Test
    void rejectsAnEmptyOrMissingBandList() {
        assertThrows(IllegalArgumentException.class, () -> new TaxCalculator(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new TaxCalculator(null));
    }

    @Test
    void keepsItsOwnCopyOfTheBands() {
        List<TaxBand> bands = new ArrayList<>(StandardTaxBands.all());
        TaxCalculator calculator = new TaxCalculator(bands);

        bands.clear();

        assertEquals(bd("40.00"), calculator.taxFor(bd("800.00")));
    }
}