package com.simplex.mario_simplex.backend;

public class App {
  public static void main(String[] args) throws Exception {
    // String exp = "1x1 + 1x2 >= 6";
    // String exp_1 = "1x1 + 1x2 <= 4";
    // String objFunction = "1x1 + 1x2";
    // String conditions = "x1 >= 0 , x2 >= 0";
    // Constraint c = new Constraint(exp);
    // Constraint c2 = new Constraint(exp_1);

    // SimplexSolver solver = new SimplexSolver(conditions);
    // solver.addConstraint(c);
    // solver.addConstraint(c2);
    // solver.show_constraints();
    // solver.objective_function(objFunction, true);
    // solver.matrix_form();
    // solver.solve();
    Integer[][] Operation =new Integer[][] {{12,13,4,6}
        ,{6,4,10,11}
        ,{10,9,12,4}};
    Integer[] supply = new Integer[] {500,700,800};
    Integer[] demand = new Integer[] {400,900,200,500};
    Transportation_Simplex Ts =  new Transportation_Simplex(Operation, supply, demand);
    Ts.NorthWest_initialization();
    Ts.solve();
  }

}
