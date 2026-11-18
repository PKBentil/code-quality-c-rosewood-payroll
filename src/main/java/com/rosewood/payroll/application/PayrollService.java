package com.rosewood.payroll.application;

import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.domain.model.Payslip;
import com.rosewood.payroll.domain.pay.GrossPayCalculator;
import com.rosewood.payroll.domain.tax.TaxCalculator;
import com.rosewood.payroll.exceptions.EmployeeNotFoundException;
import com.rosewood.payroll.port.EmployeeRepository;
import com.rosewood.payroll.validation.PayInputValidator;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Produces a payslip: validate the input, find the employee, work out gross, tax and net.
 * It only coordinates; every rule lives in a collaborator it is given. It never swallows a failure:
 * the caller receives a PayrollException subtype, or whatever the repository threw.
 */
public class PayrollService {

    private static final Logger LOG = Logger.getLogger(PayrollService.class.getName());

    private final EmployeeRepository employeeRepository;
    private final PayInputValidator validator;
    private final GrossPayCalculator grossPayCalculator;
    private final TaxCalculator taxCalculator;

    public PayrollService(EmployeeRepository employeeRepository,
                          PayInputValidator validator,
                          GrossPayCalculator grossPayCalculator,
                          TaxCalculator taxCalculator) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "employeeRepository");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.grossPayCalculator = Objects.requireNonNull(grossPayCalculator, "grossPayCalculator");
        this.taxCalculator = Objects.requireNonNull(taxCalculator, "taxCalculator");
    }

    public Payslip calculatePayslip(String employeeId, double hoursWorked, BigDecimal hourlyRate) {
        validator.validateEmployeeId(employeeId);
        validator.validateHours(hoursWorked);
        validator.validateRate(hourlyRate);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> employeeNotFound(employeeId));

        BigDecimal gross = grossPayCalculator.calculate(hoursWorked, hourlyRate);
        BigDecimal tax = taxCalculator.taxFor(gross);

        return Payslip.of(employee, gross, tax);
    }

    private static EmployeeNotFoundException employeeNotFound(String employeeId) {
        LOG.log(Level.WARNING, "No payslip produced: no employee with id {0}", employeeId);
        return new EmployeeNotFoundException(employeeId);
    }
}
