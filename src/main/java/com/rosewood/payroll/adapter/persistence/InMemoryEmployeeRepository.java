package com.rosewood.payroll.adapter.persistence;

import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.port.EmployeeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees;

    public InMemoryEmployeeRepository(Collection<Employee> source) {
        Map<String, Employee> byId = new HashMap<>();
        for (Employee employee : Objects.requireNonNull(source, "source")) {
            String id = Objects.requireNonNull(employee, "employee").getId();
            Objects.requireNonNull(id, "employee id");
            if (byId.putIfAbsent(id, employee) != null) {
                throw new IllegalArgumentException("Duplicate employee id: " + id);
            }
        }
        this.employees = Map.copyOf(byId);
    }

    @Override
    public Optional<Employee> findById(String id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(employees.get(id));
    }
}
