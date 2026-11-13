package com.rosewood.payroll.application;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.port.EmployeeRepository;

public class PayrollCalculator {

    // The calculator makes its own repository, so callers and tests cannot give it another.
    private final EmployeeRepository repository = new InMemoryEmployeeRepository();

    public double calculateNetPay(String employeeId, double hoursWorked, double hourlyRate) {
        try {
            Employee employee = repository.findById(employeeId);

            double gross = hoursWorked * hourlyRate;

            // overtime: extra hours beyond 40 are paid at time-and-a-half
            if (hoursWorked > 40) {
                gross = gross + (hoursWorked - 40) * hourlyRate * 0.5;
            }

            // tax band, worked out from gross pay
            double taxRate;
            if (gross < 1000) {
                taxRate = 0.05;      // LOW
            } else if (gross < 5000) {
                taxRate = 0.15;      // MEDIUM
            } else {
                taxRate = 0.25;      // HIGH
            }

            double net = gross - (gross * taxRate);
            System.out.println("Net pay for " + employee.getName() + ": GHS " + net);
            return net;
        } catch (Exception e) {
            // something went wrong (for example the employee was not found) - return zero
            return 0;
        }
    }
}
