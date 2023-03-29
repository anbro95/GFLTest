package com.example.gfltest;

import jakarta.xml.bind.ValidationException;
import org.apache.el.util.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class EquationServiceTests {

    private EquationService service;

    private EquationRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(EquationRepository.class);
        service = new EquationService(repository);
    }

    @Test
    void saveValid() {
        Equation equation =  Equation.builder().id(1L).equation("(6-x)+(12-x)-(3+-2*x)=15").build();
        when(repository.save(equation)).thenReturn(equation);
        Equation result =  service.save(equation, 0.0);
        Assertions.assertEquals(equation, result);
        verify(repository).save(equation);
    }

    @Test
    void saveInvalidRoot() {
        Equation equation = Equation.builder().id(1L).equation("5*x=10").build();
        when(repository.save(equation)).thenReturn(equation);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            service.save(equation, 15.0);
        });
        Assertions.assertEquals("Validation failed", thrown.getMessage());
        verify(repository, never()).save(equation);

    }

    @Test
    void saveInvalidBrackets() {
        Equation equation = Equation.builder().id(1L).equation("x+(-5+9)+7=15)").build();
        when(repository.save(equation)).thenReturn(equation);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            service.save(equation, null);
        });
        Assertions.assertEquals("Validation failed", thrown.getMessage());
        verify(repository, never()).save(equation);
    }

    @Test
    void saveInvalidSigns() {
        Equation equation = Equation.builder().id(1L).equation("15*x+9*+13=20").build();
        when(repository.save(equation)).thenReturn(equation);

        ValidationException thrown = Assertions.assertThrows(ValidationException.class, () -> {
            service.save(equation, 15.0);
        });
        Assertions.assertEquals("Validation failed", thrown.getMessage());
        verify(repository, never()).save(equation);
    }
}
