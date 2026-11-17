package com.rosewood.payroll;


import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TaxBandCoverageTest {

    private static final BigDecimal ONE_PESEWA = new BigDecimal("0.01");
    private static final BigDecimal SWEEP_LIMIT = new BigDecimal("20000.00");

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    static void assertExactlyOneBandAppliesToEveryAmount(List<TaxBand> bands, BigDecimal upTo) {
        for (BigDecimal gross = BigDecimal.ZERO; gross.compareTo(upTo) <= 0; gross = gross.add(ONE_PESEWA)) {
            int matching = 0;
            for (TaxBand band : bands) {
                if (band.appliesTo(gross)) {
                    matching++;
                }
            }
            if (matching != 1) {
                fail("Gross pay " + gross + " is covered by " + matching + " tax bands; expected exactly 1");
            }
        }
    }

    @Test
    void theStandardBandsLeaveNoGapAndNoOverlap() {
        assertExactlyOneBandAppliesToEveryAmount(StandardTaxBands.all(), SWEEP_LIMIT);
    }

    @Test
    void theSweepCatchesAGapBetweenTwoBands() {
        List<TaxBand> withGap = List.of(
                FlatRateTaxBand.between(bd("0"), bd("1000"), bd("0.05")),
                FlatRateTaxBand.from(bd("1000.01"), bd("0.15")));

        AssertionError error = assertThrows(AssertionError.class,
                () -> assertExactlyOneBandAppliesToEveryAmount(withGap, SWEEP_LIMIT));

        assertTrue(error.getMessage().contains("Gross pay 1000.00 is covered by 0 tax bands"));
    }

    @Test
    void theSweepCatchesAnOverlapBetweenTwoBands() {
        List<TaxBand> overlapping = List.of(
                FlatRateTaxBand.between(bd("0"), bd("1000"), bd("0.05")),
                FlatRateTaxBand.from(bd("999.99"), bd("0.15")));

        AssertionError error = assertThrows(AssertionError.class,
                () -> assertExactlyOneBandAppliesToEveryAmount(overlapping, SWEEP_LIMIT));

        assertTrue(error.getMessage().contains("Gross pay 999.99 is covered by 2 tax bands"));
    }
}