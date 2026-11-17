package com.rosewood.payroll.exceptions;

public class PayrollException extends RuntimeException{

    public PayrollException(String message) {
        super(message);
    }

    public PayrollException(String message, Throwable cause) {
        super(message, cause);
    }
}
