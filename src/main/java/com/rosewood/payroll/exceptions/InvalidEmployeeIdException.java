package com.rosewood.payroll.exceptions;

public class InvalidEmployeeIdException extends PayrollException {

    private final String employeeId;

    public InvalidEmployeeIdException(String employeeId, String reason) {
        super("Invalid employee id '" + employeeId + "': " + reason);
        this.employeeId = employeeId;
    }

    public String getEmployeeId(){
        return  employeeId;
    }
}

