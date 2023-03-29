package com.example.gfltest;

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
        return ResponseEntity.ok().body(equationService.save(equation, root));
    }
//  todo
    @GetMapping("/{root}")
    public ResponseEntity<List<Equation>> getAllByRoot(@PathVariable Double root) {
        return ResponseEntity.ok().body(equationRepository.findAllByRoot(root));
    }
}
