package com.rosewood.payroll;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEmployeeRepositoryTest {

    private final InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository(SampleStaff.all());

    @Test
    void findsEachEmployeeByTheirId() {
        assertSame(SampleStaff.EFUA, repository.findById("e1").orElseThrow());
        assertSame(SampleStaff.KWAME, repository.findById("e2").orElseThrow());
    }

    @Test
    void anUnknownIdIsAbsentNotNullAndNotAnError() {
        assertEquals(Optional.empty(), repository.findById("zzz"));
    }

    @Test
    void aMissingIdIsRejectedAsAProgrammingError() {
        assertThrows(NullPointerException.class, () -> repository.findById(null));
    }

    @Test
    void holdsOnlyTheEmployeesItWasBuiltWith() {
        Employee xena = new Employee("x1", "Xena Mensah");
        InMemoryEmployeeRepository small = new InMemoryEmployeeRepository(List.of(xena));

        assertSame(xena, small.findById("x1").orElseThrow());
        assertTrue(small.findById("e1").isEmpty());
    }

    @Test
    void holdsNoEmployeesUnlessGivenSome() {
        assertTrue(new InMemoryEmployeeRepository(List.of()).findById("e1").isEmpty());
    }

    @Test
    void refusesTwoEmployeesWithTheSameId() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryEmployeeRepository(
                List.of(new Employee("e1", "Efua Sarpong"), new Employee("e1", "Someone Else"))));
    }
}
