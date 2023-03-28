package com.example.gfltest;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equations")
public class EquationController {
    private final EquationService equationService;
    private final EquationRepository equationRepository;

    @PostMapping("")
    public ResponseEntity<Object> save(@RequestBody Equation equation,  @RequestParam(required = false) Double root) {
        Equation result = equationService.save(equation, root);
        if (result == null) {
            return ResponseEntity.badRequest().body("The equation is not valid");
        } else {
            return ResponseEntity.ok().body(equation);
        }
    }

    @GetMapping("/{root}")
    public ResponseEntity<List<Equation>> getAllByRoot(@PathVariable Double root) {
        return ResponseEntity.ok().body(equationRepository.findAllByRootValue(root));
    }
}
