package com.rosewood.payroll.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private Money(){
    }

    public static BigDecimal round(BigDecimal amount) {
        return amount.setScale(SCALE, ROUNDING);
    }
}
