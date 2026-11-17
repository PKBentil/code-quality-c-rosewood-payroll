package com.rosewood.payroll;

import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.domain.model.Payslip;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayslipTest {

    private final Employee employee = new Employee("e1", "Efua Sarpong");
    private final BigDecimal gross = new BigDecimal("800.00");
    private final BigDecimal tax = new BigDecimal("40.00");
    private final BigDecimal net = new BigDecimal("760.00");

    @Test
    void exposesExactlyWhatItWasBuiltWith() {
        Payslip payslip = new Payslip(employee, gross, tax, net);

        assertSame(employee, payslip.employee());
        assertEquals(gross, payslip.grossPay());
        assertEquals(tax, payslip.tax());
        assertEquals(net, payslip.netPay());
    }

    @Test
    void rejectsAnyMissingPart() {
        assertThrows(NullPointerException.class, () -> new Payslip(null, gross, tax, net));
        assertThrows(NullPointerException.class, () -> new Payslip(employee, null, tax, net));
        assertThrows(NullPointerException.class, () -> new Payslip(employee, gross, null, net));
        assertThrows(NullPointerException.class, () -> new Payslip(employee, gross, tax, null));
    }
}