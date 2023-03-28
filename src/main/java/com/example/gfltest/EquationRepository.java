package com.example.gfltest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquationRepository extends JpaRepository<Equation, Long> {
    List<Equation> findAllByRootValue(Double value);

}
