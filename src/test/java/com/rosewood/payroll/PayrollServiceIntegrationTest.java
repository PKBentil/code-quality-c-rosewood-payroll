package com.rosewood.payroll;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.application.PayrollService;
import com.rosewood.payroll.domain.model.Payslip;
import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.exceptions.EmployeeNotFoundException;
import com.rosewood.payroll.exceptions.InvalidHoursException;
import com.rosewood.payroll.validation.PayInputValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Everything real, nothing mocked: the same wiring the Demo uses. */
class PayrollServiceIntegrationTest {

    private final PayrollService service = new PayrollService(
            new InMemoryEmployeeRepository(),
            new PayInputValidator(),
            new GrossPayCalculator(),
            new TaxCalculator(StandardTaxBands.all()));

    @Test
    void paysEfuaInTheLowBand() {
        Payslip payslip = service.calculatePayslip("e1", 40, new BigDecimal("20"));

        assertEquals("Efua Sarpong", payslip.employee().getName());
        assertEquals(new BigDecimal("800.00"), payslip.grossPay());
        assertEquals(new BigDecimal("40.00"), payslip.tax());
        assertEquals(new BigDecimal("760.00"), payslip.netPay());
    }

    @Test
    void paysKwameOvertimeInTheMediumBand() {
        Payslip payslip = service.calculatePayslip("e2", 50, new BigDecimal("30"));

        assertEquals("Kwame Owusu", payslip.employee().getName());
        assertEquals(new BigDecimal("1650.00"), payslip.grossPay());
        assertEquals(new BigDecimal("247.50"), payslip.tax());
        assertEquals(new BigDecimal("1402.50"), payslip.netPay());
    }

    @Test
    void paysInTheHighBandOnceGrossReachesFiveThousand() {
        Payslip payslip = service.calculatePayslip("e1", 40, new BigDecimal("125"));

        assertEquals(new BigDecimal("5000.00"), payslip.grossPay());
        assertEquals(new BigDecimal("1250.00"), payslip.tax());
        assertEquals(new BigDecimal("3750.00"), payslip.netPay());
    }

    @Test
    void anUnknownEmployeeNowFailsInsteadOfReturningZero() {
        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class,
                () -> service.calculatePayslip("unknown", 40, new BigDecimal("20")));

        assertEquals("unknown", ex.getEmployeeId());
    }

    @Test
    void aMissingEmployeeIdIsTreatedAsUnknown() {
        assertThrows(EmployeeNotFoundException.class,
                () -> service.calculatePayslip(null, 40, new BigDecimal("20")));
    }

    @Test
    void negativeHoursNowFailInsteadOfProducingNegativePay() {
        InvalidHoursException ex = assertThrows(InvalidHoursException.class,
                () -> service.calculatePayslip("e1", -5, new BigDecimal("20")));

        assertEquals(-5.0, ex.getHours());
    }
}