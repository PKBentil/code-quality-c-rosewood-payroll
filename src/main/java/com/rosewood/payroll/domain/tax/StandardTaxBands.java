package com.rosewood.payroll.domain.tax;

import java.math.BigDecimal;
import java.util.List;

/** Rosewood's current tax bands. Adding a band means adding one entry here (or one new TaxBand type). */
public final class StandardTaxBands {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal LOW_UPPER = new BigDecimal("1000");
    private static final BigDecimal MEDIUM_UPPER = new BigDecimal("5000");

    private static final BigDecimal LOW_RATE = new BigDecimal("0.05");
    private static final BigDecimal MEDIUM_RATE = new BigDecimal("0.15");
    private static final BigDecimal HIGH_RATE = new BigDecimal("0.25");

    private StandardTaxBands() {
    }

    public static List<TaxBand> all() {
        return List.of(
                FlatRateTaxBand.between(ZERO, LOW_UPPER, LOW_RATE),
                FlatRateTaxBand.between(LOW_UPPER, MEDIUM_UPPER, MEDIUM_RATE),
                FlatRateTaxBand.from(MEDIUM_UPPER, HIGH_RATE));
    }
}