package com.rosewood.payroll;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.application.PayrollService;
import com.rosewood.payroll.domain.model.Money;
import com.rosewood.payroll.domain.model.Payslip;
import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxBand;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.validation.PayInputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewTaxBandExtensionTest {

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private static final class PremiumTaxBand implements TaxBand {

        private static final BigDecimal THRESHOLD = new BigDecimal("20000");
        private static final BigDecimal BASE_TAX = new BigDecimal("5000");
        private static final BigDecimal RATE = new BigDecimal("0.35");

        @Override
        public boolean appliesTo(BigDecimal grossPay) {
            return grossPay.compareTo(THRESHOLD) >= 0;
        }

        @Override
        public BigDecimal taxOn(BigDecimal grossPay) {
            return Money.round(BASE_TAX.add(grossPay.subtract(THRESHOLD).multiply(RATE)));
        }
    }

    private PayrollService serviceWithThePremiumBandAdded() {
        List<TaxBand> bands = new ArrayList<>();
        bands.add(new PremiumTaxBand());        // new rule, placed first so it wins above 20,000
        bands.addAll(StandardTaxBands.all());   // the existing bands, exactly as they are

        return new PayrollService(new InMemoryEmployeeRepository(), new PayInputValidator(),
                new GrossPayCalculator(), new TaxCalculator(bands));
    }

    @ParameterizedTest(name = "{0}h at {1}: gross {2}, tax {3}, net {4}")
    @CsvSource({
            "40,    500,    20000.00, 5000.00, 15000.00",   // exactly on the new threshold: 5000 + 35% of 0
            "60,    400,    28000.00, 7800.00, 20200.00",   // 5000 + 35% of 8000 = 7800
            "40,    499.99, 19999.60, 4999.90, 14999.70",   // just below: the old top band still applies
            "50,    30,     1650.00,  247.50,  1402.50",    // unchanged: medium band with overtime
            "40,    125,    5000.00,  1250.00, 3750.00"     // unchanged: top band boundary
    })
    void theNewBandTaxesLargePayAndLeavesEverythingElseAlone(
            double hours, String rate, String gross, String tax, String net) {
        Payslip payslip = serviceWithThePremiumBandAdded().calculatePayslip("e1", hours, bd(rate));

        assertEquals(bd(gross), payslip.grossPay());
        assertEquals(bd(tax), payslip.tax());
        assertEquals(bd(net), payslip.netPay());
    }

    @Test
    void whenTheOldTopBandIsBoundedTheNewSetHasNoGapAndNoOverlap() {
        // The shape to put in StandardTaxBands: the old top band gets an upper bound, the new band is added.
        List<TaxBand> bounded = List.of(
                FlatRateTaxBand.between(bd("0"), bd("1000"), bd("0.05")),
                FlatRateTaxBand.between(bd("1000"), bd("5000"), bd("0.15")),
                FlatRateTaxBand.between(bd("5000"), bd("20000"), bd("0.25")),
                new PremiumTaxBand());

        TaxBandCoverageTest.assertExactlyOneBandAppliesToEveryAmount(bounded, bd("30000.00"));
    }
}