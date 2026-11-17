package com.rosewood.payroll;

import com.rosewood.payroll.adapter.persistence.InMemoryEmployeeRepository;
import com.rosewood.payroll.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryEmployeeRepositoryTest {

    @Test
    void theDefaultRepositoryKnowsTheTwoDemoEmployees() {
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository();

        Employee efua = repository.findById("e1").orElseThrow();
        Employee kwame = repository.findById("e2").orElseThrow();

        assertEquals("e1", efua.getId());
        assertEquals("Efua Sarpong", efua.getName());
        assertEquals("e2", kwame.getId());
        assertEquals("Kwame Owusu", kwame.getName());
    }

    @Test
    void anUnknownIdIsAbsentNotNullAndNotAnError() {
        assertEquals(Optional.empty(), new InMemoryEmployeeRepository().findById("zzz"));
    }

    @Test
    void aMissingIdIsAbsent() {
        assertTrue(new InMemoryEmployeeRepository().findById(null).isEmpty());
    }

    @Test
    void aRepositoryBuiltFromAListHoldsOnlyThoseEmployees() {
        Employee xena = new Employee("x1", "Xena Mensah");
        InMemoryEmployeeRepository repository = new InMemoryEmployeeRepository(List.of(xena));

        assertSame(xena, repository.findById("x1").orElseThrow());
        assertTrue(repository.findById("e1").isEmpty());
    }
}