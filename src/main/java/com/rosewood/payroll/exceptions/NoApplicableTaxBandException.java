package com.rosewood.payroll.exceptions;

import java.math.BigDecimal;

public class NoApplicableTaxBandException extends PayrollException{
    private final BigDecimal grossPay;

    public NoApplicableTaxBandException(BigDecimal grossPay) {
        super("No tax band applies to gross pay " + grossPay);
        this.grossPay = grossPay;
    }

    public BigDecimal getGrossPay() {
        return grossPay;
    }
}
