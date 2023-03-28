package com.example.gfltest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equation")
public class EquationController {
    private final EquationService equationService;

    @PostMapping("/{root}")
    public ResponseEntity<Equation> save(@RequestBody Equation equation, @PathVariable Double root) {
        return ResponseEntity.ok().body(equationService.save(equation, root));
    }
}
