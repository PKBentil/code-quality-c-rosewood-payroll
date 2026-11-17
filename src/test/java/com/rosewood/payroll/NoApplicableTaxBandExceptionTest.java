package com.rosewood.payroll;

import com.rosewood.payroll.exceptions.NoApplicableTaxBandException;
import com.rosewood.payroll.exceptions.PayrollException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class NoApplicableTaxBandExceptionTest {

    @Test
    void carriesTheGrossPayAndSaysSo() {
        NoApplicableTaxBandException ex = new NoApplicableTaxBandException(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), ex.getGrossPay());
        assertEquals("No tax band applies to gross pay 100.00", ex.getMessage());
        assertInstanceOf(PayrollException.class, ex);
    }
}
