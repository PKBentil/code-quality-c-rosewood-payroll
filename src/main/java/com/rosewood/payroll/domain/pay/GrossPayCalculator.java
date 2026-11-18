package com.rosewood.payroll.domain.pay;

import com.rosewood.payroll.domain.model.Money;

import java.math.BigDecimal;

public class GrossPayCalculator {

    private static final BigDecimal STANDARD_HOURS = BigDecimal.valueOf(40);
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");


    public BigDecimal calculate(double hoursWorked, BigDecimal hourlyRate) {
        BigDecimal hours = BigDecimal.valueOf(hoursWorked);
        BigDecimal regularHours = hours.min(STANDARD_HOURS);
        BigDecimal overtimeHours = hours.subtract(regularHours);

        BigDecimal regularPay = regularHours.multiply(hourlyRate);
        BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);

        return Money.round(regularPay.add(overtimePay));
    }
}
