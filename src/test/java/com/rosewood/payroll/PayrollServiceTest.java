package com.rosewood.payroll;

import com.rosewood.payroll.application.PayrollService;
import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.domain.model.Payslip;
import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import com.rosewood.payroll.domain.tax.FlatRateTaxBand;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxBand;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.exceptions.EmployeeNotFoundException;
import com.rosewood.payroll.exceptions.InvalidHoursException;
import com.rosewood.payroll.exceptions.InvalidRateException;
import com.rosewood.payroll.exceptions.NoApplicableTaxBandException;
import com.rosewood.payroll.port.EmployeeRepository;
import com.rosewood.payroll.validation.PayInputValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Only the repository is a mock: it is the one real boundary. The calculators and the validator are the
 * real classes, so these tests check the pay figures, not that a mock was called.
 */
@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    private static final BigDecimal TWENTY = new BigDecimal("20");

    @Mock
    private EmployeeRepository repository;

    private final Employee efua = new Employee("e1", "Efua Sarpong");

    private PayrollService serviceWith(List<TaxBand> bands) {
        return new PayrollService(repository, new PayInputValidator(),
                new GrossPayCalculator(), new TaxCalculator(bands));
    }

    private PayrollService standardService() {
        return serviceWith(StandardTaxBands.all());
    }

    // The golden table, worked out by hand from the rules. net = gross - tax.
    @ParameterizedTest(name = "{0}h at {1}: gross {2}, tax {3}, net {4}")
    @CsvSource({
            "40,    20,      800.00,  40.00,   760.00",    // G1
            "35,    25,      875.00,  43.75,   831.25",    // G2
            "41,    20,      830.00,  41.50,   788.50",    // G3 first overtime hour
            "40.5,  20,      815.00,  40.75,   774.25",    // G4
            "50,    30,      1650.00, 247.50,  1402.50",   // G5 the figure the AI only ever mocked
            "1,     999.99,  999.99,  50.00,   949.99",    // G6 just below 1000, tax rounded up
            "40,    25,      1000.00, 150.00,  850.00",    // G7 exactly 1000, next band
            "1,     4999.99, 4999.99, 750.00,  4249.99",   // G8 just below 5000, tax rounded up
            "40,    125,     5000.00, 1250.00, 3750.00",   // G9 exactly 5000, top band
            "60,    100,     7000.00, 1750.00, 5250.00",   // G10 top band with overtime
            "0,     20,      0.00,    0.00,    0.00"       // G11 a valid zero is not a failure
    })
    void producesTheExpectedPayslip(double hours, String rate, String gross, String tax, String net) {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));

        Payslip payslip = standardService().calculatePayslip("e1", hours, new BigDecimal(rate));

        assertAll(
                () -> assertSame(efua, payslip.employee()),
                () -> assertEquals(new BigDecimal(gross), payslip.grossPay()),
                () -> assertEquals(new BigDecimal(tax), payslip.tax()),
                () -> assertEquals(new BigDecimal(net), payslip.netPay()));
    }

    @Test
    void usesTheTaxBandsItWasGivenNotAFixedSet() {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));
        PayrollService flatTenPercent =
                serviceWith(List.of(FlatRateTaxBand.from(BigDecimal.ZERO, new BigDecimal("0.10"))));

        Payslip payslip = flatTenPercent.calculatePayslip("e1", 40, TWENTY);

        assertEquals(new BigDecimal("800.00"), payslip.grossPay());
        assertEquals(new BigDecimal("80.00"), payslip.tax());
        assertEquals(new BigDecimal("720.00"), payslip.netPay());
    }

    @Test
    void anUnknownEmployeeIsAnErrorNotAZeroPayslip() {
        when(repository.findById("zzz")).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class,
                () -> standardService().calculatePayslip("zzz", 40, TWENTY));

        assertEquals("zzz", ex.getEmployeeId());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -40.0, Double.NaN, Double.POSITIVE_INFINITY})
    void invalidHoursAreRejectedBeforeAnyLookup(double hours) {
        assertThrows(InvalidHoursException.class,
                () -> standardService().calculatePayslip("e1", hours, TWENTY));

        verifyNoInteractions(repository);
    }

    @Test
    void aMissingRateIsRejectedBeforeAnyLookup() {
        assertThrows(InvalidRateException.class,
                () -> standardService().calculatePayslip("e1", 40, null));

        verifyNoInteractions(repository);
    }

    @Test
    void aNegativeRateIsRejectedBeforeAnyLookup() {
        assertThrows(InvalidRateException.class,
                () -> standardService().calculatePayslip("e1", 40, new BigDecimal("-20")));

        verifyNoInteractions(repository);
    }

    @Test
    void aRepositoryFailureReachesTheCallerUntouched() {
        IllegalStateException failure = new IllegalStateException("store is down");
        when(repository.findById("e1")).thenThrow(failure);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> standardService().calculatePayslip("e1", 40, TWENTY));

        assertSame(failure, thrown);
    }

    @Test
    void aGapInTheTaxBandsReachesTheCallerInsteadOfBeingTaxedAsZero() {
        when(repository.findById("e1")).thenReturn(Optional.of(efua));
        PayrollService narrowBands = serviceWith(
                List.of(FlatRateTaxBand.between(BigDecimal.ZERO, new BigDecimal("100"), new BigDecimal("0.05"))));

        NoApplicableTaxBandException ex = assertThrows(NoApplicableTaxBandException.class,
                () -> narrowBands.calculatePayslip("e1", 40, TWENTY));

        assertEquals(new BigDecimal("800.00"), ex.getGrossPay());
    }

    @Test
    void refusesToBeBuiltWithoutItsCollaborators() {
        PayInputValidator validator = new PayInputValidator();
        GrossPayCalculator gross = new GrossPayCalculator();
        TaxCalculator tax = new TaxCalculator(StandardTaxBands.all());

        assertThrows(NullPointerException.class, () -> new PayrollService(null, validator, gross, tax));
        assertThrows(NullPointerException.class, () -> new PayrollService(repository, null, gross, tax));
        assertThrows(NullPointerException.class, () -> new PayrollService(repository, validator, null, tax));
        assertThrows(NullPointerException.class, () -> new PayrollService(repository, validator, gross, null));
    }
}