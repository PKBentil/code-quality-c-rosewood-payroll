package com.rosewood.payroll;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.application.PayrollService;
import com.rosewood.payroll.domain.model.Payslip;
import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import com.rosewood.payroll.domain.tax.StandardTaxBands;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.exceptions.PayrollException;
import com.rosewood.payroll.validation.PayInputValidator;

import java.math.BigDecimal;

public class Demo {
    public static void main(String[] args) {
        PayrollService service = new PayrollService(
                new InMemoryEmployeeRepository(),
                new PayInputValidator(),
                new GrossPayCalculator(),
                new TaxCalculator(StandardTaxBands.all())
        );

        show(service, "e1",40, "20");
        show(service, "e2",50, "30");
        show(service, "unknown",40, "20");
        show(service, "e1",-5, "20");
    }

    private static void show(PayrollService service, String employeeId, double hours, String rate) {
        try {
            Payslip payslip = service.calculatePayslip(employeeId, hours, new BigDecimal(rate));
            System.out.printf("%s: gross GHS %s, tax GHS %s, net GHS %s%n",
                    payslip.employee().getName(),
                    payslip.grossPay().toPlainString(),
                    payslip.tax().toPlainString(),
                    payslip.netPay().toPlainString());

        } catch (PayrollException e) {
            System.out.println("Could not calculate pay for '" + employeeId + "': " + e.getMessage());
        }
    }
}
