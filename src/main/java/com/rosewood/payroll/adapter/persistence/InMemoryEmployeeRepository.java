package com.rosewood.payroll.adapter.persistence;

import com.rosewood.payroll.domain.model.Employee;
import com.rosewood.payroll.port.EmployeeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    public InMemoryEmployeeRepository() {
        employees.put("e1", new Employee("e1", "Efua Sarpong"));
        employees.put("e2", new Employee("e2", "Kwame Owusu"));
    }

    public InMemoryEmployeeRepository(Collection<Employee> employees){
        for (Employee employee : employees) {
            this.employees.put(employee.getId(), employee);
        }
    }

    @Override
    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employees.get(id));
    }
}
