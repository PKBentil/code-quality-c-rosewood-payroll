package com.rosewood.payroll.port;

import com.rosewood.payroll.domain.model.Employee;

public interface EmployeeRepository {
    Employee findById(String id);
}
