package com.simplex.mario_simplex.backend;

public class App {
    public static void main(String[] args) throws Exception {
        String exp = "x1 + 4x2 + 5x3 = 8";
        String exp_1 = "20x1 + 9x2 + 10x3 = 0";
        String conditions = "x1 = 0 , x2 = 0 ,x3 =0";
        String objFunction = "5x1 + 10x2 + -5x3";
        constraint c = new constraint(exp);
        constraint c2 = new constraint(exp_1);
        
        SimplexSolver solver = new SimplexSolver(conditions);
        solver.addConstraint(c);
        solver.addConstraint(c2);
        solver.show_constraints();
        solver.objective_function(objFunction, true);
        solver.show_sorted();
        solver.matrix_form();
    }

}
