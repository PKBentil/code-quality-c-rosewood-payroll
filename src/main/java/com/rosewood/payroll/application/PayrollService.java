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

/**
 * Produces a payslip: validate the input, find the employee, work out gross, tax and net.
 * It only coordinates; every rule lives in a collaborator it is given. Failures are never caught here,
 * they reach the caller as PayrollExceptions.
 */
public class PayrollService {

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
        validator.validateHours(hoursWorked);
        validator.validateRate(hourlyRate);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        BigDecimal gross = grossPayCalculator.calculate(hoursWorked, hourlyRate);
        BigDecimal tax = taxCalculator.taxFor(gross);

        return new Payslip(employee, gross, tax, gross.subtract(tax));
    }
}