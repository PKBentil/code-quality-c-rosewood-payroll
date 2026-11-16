package com.rosewood.payroll.domain.tax;

import com.rosewood.payroll.domain.model.Money;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Taxes the whole gross pay at one rate when it falls in [lowerInclusive, upperExclusive).
 * An open-ended band has no upper bound. This is the current Rosewood rule: the rate applies to the
 * whole amount, not just the slice above the threshold (see ANALYSIS.md, open business question).
 */
public final class FlatRateTaxBand implements TaxBand {

    private final BigDecimal lowerInclusive;
    private final BigDecimal upperExclusive; // null means no upper bound
    private final BigDecimal rate;

    private FlatRateTaxBand(BigDecimal lowerInclusive, BigDecimal upperExclusive, BigDecimal rate) {
        Objects.requireNonNull(lowerInclusive, "lowerInclusive");
        Objects.requireNonNull(rate, "rate");
        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1: " + rate);
        }
        if (upperExclusive != null && upperExclusive.compareTo(lowerInclusive) <= 0) {
            throw new IllegalArgumentException(
                    "Upper bound must be above lower bound: " + lowerInclusive + " to " + upperExclusive);
        }
        this.lowerInclusive = lowerInclusive;
        this.upperExclusive = upperExclusive;
        this.rate = rate;
    }

    public static FlatRateTaxBand between(BigDecimal lowerInclusive, BigDecimal upperExclusive, BigDecimal rate) {
        Objects.requireNonNull(upperExclusive, "upperExclusive");
        return new FlatRateTaxBand(lowerInclusive, upperExclusive, rate);
    }

    public static FlatRateTaxBand from(BigDecimal lowerInclusive, BigDecimal rate) {
        return new FlatRateTaxBand(lowerInclusive, null, rate);
    }

    @Override
    public boolean appliesTo(BigDecimal grossPay) {
        return grossPay.compareTo(lowerInclusive) >= 0
                && (upperExclusive == null || grossPay.compareTo(upperExclusive) < 0);
    }

    @Override
    public BigDecimal taxOn(BigDecimal grossPay) {
        return Money.round(grossPay.multiply(rate));
    }
}