package com.rosewood.payroll.port;

import com.rosewood.payroll.domain.model.Employee;

import java.util.Optional;

public interface EmployeeRepository {
   Optional<Employee> findById(String id);
}
