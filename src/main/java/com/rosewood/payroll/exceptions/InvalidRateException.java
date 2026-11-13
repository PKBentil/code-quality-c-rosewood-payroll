package com.rosewood.payroll.exceptions;

import java.math.BigDecimal;

public class InvalidRateException extends PayrollException{
    private final BigDecimal rate;

    public InvalidRateException(BigDecimal rate, String reason) {
        super("Invalid hourly rate: " + rate + ": " + reason);
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
