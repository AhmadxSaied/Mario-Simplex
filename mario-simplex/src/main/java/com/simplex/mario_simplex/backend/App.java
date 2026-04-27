package com.simplex.mario_simplex.backend;

public class App {
  public static void main(String[] args) throws Exception {
    String exp = "1x1 + 2x2 = 10";
    String exp_1 = "3x1 + 1x2 = 15";
    String objFunction = "5x1 + 4x2";
    String conditions = "x1 >= 0 , x2 >= 0 ,x3 >=0";
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
