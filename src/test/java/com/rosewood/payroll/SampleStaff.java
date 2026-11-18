package com.rosewood.payroll;

import com.rosewood.payroll.domain.model.Employee;

import java.util.List;

final class SampleStaff {

    static final Employee EFUA = new Employee("e1", "Efua Sarpong");
    static final Employee KWAME = new Employee("e2", "Kwame Owusu");

    private SampleStaff(){
    }

    static List<Employee> all() {
        return List.of(EFUA, KWAME);
    }
}
