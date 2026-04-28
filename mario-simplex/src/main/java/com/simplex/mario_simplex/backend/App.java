package com.simplex.mario_simplex.backend;

public class App {
  public static void main(String[] args) throws Exception {
    String exp = "4x1 + 5x2 + 6x3 >= 12";
    String exp_1 = "9x1 + 5x2 + 2x3 <= 80";
    String objFunction = "4x1 + 5x2 + 6x3";
    String conditions = "x1 >= 0 , x2 == 0 ,x3 >=0";
    Constraint c = new Constraint(exp);
    Constraint c2 = new Constraint(exp_1);

    SimplexSolver solver = new SimplexSolver(conditions);
    solver.addConstraint(c);
    solver.addConstraint(c2);
    solver.show_constraints();
    solver.objective_function(objFunction, true);
    solver.matrix_form();
    solver.solve();
  }

}
