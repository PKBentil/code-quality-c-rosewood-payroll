package com.rosewood.payroll;

public interface EmployeeRepository {
    Employee findById(String id);
}
