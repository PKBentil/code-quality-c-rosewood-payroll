package com.rosewood.payroll.exceptions;

public class InvalidHoursException extends PayrollException {

    private final double hours;

    public InvalidHoursException(double hours, String reason) {
        super("Invalid hours " + hours + ": " + reason);
        this.hours = hours;
    }

    public double getHours(){
        return hours;
    }
}

