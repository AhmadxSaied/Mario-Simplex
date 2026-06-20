package com.simplex.mario_simplex.backend;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.simplex.mario_simplex.backend.Data.SimplexData;
import com.simplex.mario_simplex.backend.Data.SimplexResult;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class MarioController {

    @PostMapping("/api/solve")
    public ResponseEntity<List<SimplexResult>> solveSimplex(@RequestBody SimplexData data) {
        // solve the simplex problem using the data provided
        String boundStr = String.join(", ", data.getBounds());

        SimplexSolver solver = new SimplexSolver(boundStr);
        if (data.getConstraints() != null) {
            for (String constraint : data.getConstraints()) {
                solver.addConstraint(new Constraint(constraint));
            }
        }

        try {

            solver.objective_function(data.getObjectiveFunction(), data.getObjectiveType().equalsIgnoreCase("max"));
            solver.matrix_form();
            List<SimplexResult> results = solver.solve();
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.out.println("Error solving simplex problem: " + e.getMessage());
        }

        return ResponseEntity.badRequest().build();
    }
}
