package com.rosewood.payroll.validation;

import com.rosewood.payroll.exceptions.InvalidEmployeeIdException;
import com.rosewood.payroll.exceptions.InvalidHoursException;
import com.rosewood.payroll.exceptions.InvalidRateException;

import java.math.BigDecimal;


public class PayInputValidator {

    public static final double MAX_HOURS = 168.0;

    public static final BigDecimal MAX_RATE = new BigDecimal("10000");

    public void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new InvalidEmployeeIdException(employeeId, "must not be blank");
        }
    }

    public void validateHours(double hours) {
        if (Double.isNaN(hours) || Double.isInfinite(hours)) {
            throw new InvalidHoursException(hours, "must be a finite number");
        }
        if (hours < 0) {
            throw new InvalidHoursException(hours, "must not be negative");
        }
        if (hours > MAX_HOURS) {
            throw new InvalidHoursException(hours, "must not exceed " + MAX_HOURS);
        }
    }

    public void validateRate(BigDecimal rate) {
        if (rate == null) {
            throw new InvalidRateException(null, "must be provided");
        }
        if (rate.signum() < 0) {
            throw new InvalidRateException(rate, "must not be negative");
        }
        if (rate.compareTo(MAX_RATE) > 0) {
            throw new InvalidRateException(rate, "must not exceed " + MAX_RATE.toPlainString());
        }
    }
}
