package com.rosewood.payroll.exceptions;

public class EmployeeNotFoundException extends PayrollException{

    private final String employeeId;

    public EmployeeNotFoundException(String message, String employeeId) {
        super("Employee not found: " + employeeId);
        this.employeeId = employeeId;
    }

    public String getEmployeeId(){
        return employeeId;
    }
}
