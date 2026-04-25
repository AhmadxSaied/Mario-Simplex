package com.simplex.mario_simplex.backend;

public class App {
  public static void main(String[] args) throws Exception {
    String exp = "1x1 + 2x2 <= 10";
    String exp_1 = "3x1 + 1x2 <= 15";
    String objFunction = "5x1 + 4x2";
    String conditions = "x1 >= 0 , x2 >= 0 ,x3 >=0";
    constraint c = new constraint(exp);
    constraint c2 = new constraint(exp_1);

    StandardSimplexSolver solver = new StandardSimplexSolver(conditions);
    solver.addConstraint(c);
    solver.addConstraint(c2);
    solver.show_constraints();
    solver.objective_function(objFunction, true);
    solver.matrix_form();
    solver.solveStandard();
  }

}
