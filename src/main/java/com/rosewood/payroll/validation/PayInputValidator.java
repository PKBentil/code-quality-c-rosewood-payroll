package com.rosewood.payroll.validation;

import com.rosewood.payroll.exceptions.InvalidHoursException;
import com.rosewood.payroll.exceptions.InvalidRateException;

import java.math.BigDecimal;

public class PayInputValidator {

    public void validateHours(double hours){
        if(Double.isNaN(hours) || Double.isInfinite(hours)) {
            throw new InvalidHoursException(hours, "must be a finite number");
        }

        if (hours < 0){
            throw new InvalidHoursException(hours, "must not be negative");
        }
    }

    public void validateRate(BigDecimal rate){
        if (rate == null){
            throw new InvalidRateException(null, "must be provided");
        }

        if (rate.signum() < 0){
            throw new InvalidRateException(rate, "must not be negative");
        }
    }
}
