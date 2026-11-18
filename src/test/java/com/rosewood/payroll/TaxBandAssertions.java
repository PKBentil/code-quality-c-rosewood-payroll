package com.rosewood.payroll;

import com.rosewood.payroll.domain.tax.TaxBand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

final class TaxBandAssertions {

    private static final BigDecimal PESEWA = new BigDecimal("0.01");
    private static final BigDecimal GRID_STEP = new BigDecimal("1.00");

    private TaxBandAssertions() {
    }

    static void assertExactlyOneBandApplies(List<TaxBand> bands, BigDecimal upTo, BigDecimal... boundaries) {
        for (BigDecimal gross : probes(upTo, boundaries)) {
            long matching = bands.stream().filter(band -> band.appliesTo(gross)).count();
            if (matching != 1) {
                throw new AssertionError("Gross pay " + gross + " is covered by " + matching
                        + " tax bands; expected exactly 1");
            }
        }
    }

    private static List<BigDecimal> probes(BigDecimal upTo, BigDecimal... boundaries) {
        BigDecimal limit = upTo.setScale(2);
        List<BigDecimal> probes = new ArrayList<>();

        for (BigDecimal gross = BigDecimal.ZERO.setScale(2); gross.compareTo(limit) <= 0; gross = gross.add(GRID_STEP)) {
            probes.add(gross);
        }
        for (BigDecimal boundary : boundaries) {
            BigDecimal edge = boundary.setScale(2);
            for (BigDecimal probe : List.of(edge.subtract(PESEWA), edge, edge.add(PESEWA))) {
                if (probe.signum() >= 0 && probe.compareTo(limit) <= 0) {
                    probes.add(probe);
                }
            }
        }
        return probes;
    }
}
