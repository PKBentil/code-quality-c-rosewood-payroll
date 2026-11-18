package com.rosewood.payroll.domain.tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public final class StandardTaxBands {

    private record Tier(BigDecimal lowerInclusive, BigDecimal rate) {
    }

    private static final List<Tier> TIERS = List.of(
            new Tier(new BigDecimal("0"), new BigDecimal("0.05")),
            new Tier(new BigDecimal("1000"), new BigDecimal("0.15")),
            new Tier(new BigDecimal("5000"), new BigDecimal("0.25")));

    private StandardTaxBands() {
    }

    public static List<TaxBand> all() {
        List<TaxBand> bands = new ArrayList<>();
        for (int i = 0; i < TIERS.size(); i++) {
            Tier tier = TIERS.get(i);
            boolean isLast = i == TIERS.size() - 1;
            bands.add(isLast
                    ? FlatRateTaxBand.from(tier.lowerInclusive(), tier.rate())
                    : FlatRateTaxBand.between(tier.lowerInclusive(), TIERS.get(i + 1).lowerInclusive(), tier.rate()));
        }
        return List.copyOf(bands);
    }
}
