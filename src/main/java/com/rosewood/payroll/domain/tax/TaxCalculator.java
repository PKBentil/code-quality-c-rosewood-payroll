package com.rosewood.payroll.domain.tax;


import com.rosewood.payroll.exceptions.NoApplicableTaxBandException;

import java.math.BigDecimal;
import java.util.List;

/** Picks the first band that applies to a gross pay amount and asks it for the tax. Bands are injected. */
public class TaxCalculator {

    private final List<TaxBand> bands;

    public TaxCalculator(List<TaxBand> bands) {
        if (bands == null || bands.isEmpty()) {
            throw new IllegalArgumentException("At least one tax band is required");
        }
        this.bands = List.copyOf(bands);
    }

    public BigDecimal taxFor(BigDecimal grossPay) {
        return bands.stream()
                .filter(band -> band.appliesTo(grossPay))
                .findFirst()
                .map(band -> band.taxOn(grossPay))
                .orElseThrow(() -> new NoApplicableTaxBandException(grossPay));
    }
}