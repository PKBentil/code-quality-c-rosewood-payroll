package com.rosewood.payroll;

import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxBandCoverageTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    @Test
    void theStandardBandsLeaveNoGapAndNoOverlap() {
        TaxBandAssertions.assertExactlyOneBandApplies(
                StandardTaxBands.all(), bd("20000"), bd("1000"), bd("5000"));
    }

    @Test
    void theCheckCatchesAGapBetweenTwoBands() {
        List<TaxBand> withGap = List.of(
                FlatRateTaxBand.between(bd("0"), bd("1000"), bd("0.05")),
                FlatRateTaxBand.from(bd("1000.01"), bd("0.15")));

        AssertionError error = assertThrows(AssertionError.class,
                () -> TaxBandAssertions.assertExactlyOneBandApplies(withGap, bd("20000"), bd("1000"), bd("1000.01")));

        assertTrue(error.getMessage().contains("Gross pay 1000.00 is covered by 0 tax bands"));
    }

    @Test
    void theCheckCatchesAnOverlapBetweenTwoBands() {
        List<TaxBand> overlapping = List.of(
                FlatRateTaxBand.between(bd("0"), bd("1000"), bd("0.05")),
                FlatRateTaxBand.from(bd("999.99"), bd("0.15")));

        AssertionError error = assertThrows(AssertionError.class,
                () -> TaxBandAssertions.assertExactlyOneBandApplies(overlapping, bd("20000"), bd("999.99"), bd("1000")));

        assertTrue(error.getMessage().contains("Gross pay 999.99 is covered by 2 tax bands"));
    }
}
