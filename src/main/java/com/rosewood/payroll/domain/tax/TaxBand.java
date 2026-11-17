package com.rosewood.payroll.domain.tax;

import java.math.BigDecimal;

public interface TaxBand {

    boolean appliesTo(BigDecimal grossPay);

    BigDecimal taxOn(BigDecimal grossPay);
}
