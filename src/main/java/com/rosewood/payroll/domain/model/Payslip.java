package com.rosewood.payroll.domain.model;

import javax.management.ObjectName;
import java.math.BigDecimal;
import java.util.Objects;

public record Payslip(Employee employee, BigDecimal grossPay,BigDecimal tax, BigDecimal netPay){
    public Payslip{
        Objects.requireNonNull(employee, "employee");
        Objects.requireNonNull(grossPay, "grossPay");
        Objects.requireNonNull(tax, "tax");
        Objects.requireNonNull(netPay, "netPay");
    }
}
