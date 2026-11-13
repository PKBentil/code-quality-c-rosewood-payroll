package com.rosewood.payroll.adapter.persistence;

import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.port.EmployeeRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    public InMemoryEmployeeRepository() {
        employees.put("e1", new Employee("e1", "Efua Sarpong"));
        employees.put("e2", new Employee("e2", "Kwame Owusu"));
    }

    @Override
    public Employee findById(String id) {
        return employees.get(id); // returns null when the id is not found
    }
}
