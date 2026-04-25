package com.simplex.mario_simplex.backend;
import java.util.Map;

public class StandardSimplexSolver extends SimplexSolver {

  // Constructor

  public StandardSimplexSolver(String conditions) throws Exception {

    // This calls the parent SimplexSolver constructor first!

    super(conditions);

  }

  public void solveStandard() {

    int rows = this.operation_Matrix.length;

    int cols = this.z_row.length;

    // calculating the inital cj-zj row

    double[] cj_zj = new double[cols];

    for (int i = 0; i < cols; i++) {

      double zj = 0.0;

      for (int j = 0; j < rows; j++) {

        String basic_var = this.basic_variables[j];

        int var_idx = this.variable_indeces.get(basic_var);

        double cb = this.z_row[var_idx];

        zj += cb * this.operation_Matrix[j][i];

      }

      cj_zj[i] = this.z_row[i] - zj;

    }

    this.z_row = cj_zj;

    while (true) {

      int max_Pos_idx = 0;

      for (int i = 1; i < cols; i++) {

        if (this.z_row[i] > this.z_row[max_Pos_idx]) {

          max_Pos_idx = i;

        }

      }

      if (this.z_row[max_Pos_idx] <= 0) {

        break;

      }

      // ratio test

      int out_idx = 0;

      double out_value = Integer.MAX_VALUE;

      for (int i = 0; i < rows; i++) {

        double pivot_elemnt = this.operation_Matrix[i][max_Pos_idx];

        if (pivot_elemnt > 0) {

          double ratio = this.result_arr[i] / pivot_elemnt;

          if (ratio < out_value) {

            out_idx = i;

            out_value = ratio;

          }

        }

      }

      if (out_value == Integer.MAX_VALUE) {

        break;

      }

      // swap base

      String entering_var_name = "";

      for (Map.Entry<String, Integer> entry : this.variable_indeces.entrySet()) {

        if (entry.getValue() == max_Pos_idx) {

          entering_var_name = entry.getKey();

          break;

        }

      }

      this.basic_variables[out_idx] = entering_var_name;

      // divide the matrix values and the result arr

      double pivot_value = this.operation_Matrix[out_idx][max_Pos_idx];

      for (int i = 0; i < cols; i++) {

        this.operation_Matrix[out_idx][i] /= pivot_value;

      }

      this.result_arr[out_idx] /= pivot_value;

      for (int i = 0; i < rows; i++) {

        if (i == out_idx) {

          continue;

        }

        double multipiler = this.operation_Matrix[i][max_Pos_idx];

        for (int j = 0; j < cols; j++) {

          this.operation_Matrix[i][j] -= (multipiler * this.operation_Matrix[out_idx][j]);

        }

        this.result_arr[i] -= (multipiler * this.result_arr[out_idx]);

      }

      double z_multipiler = this.z_row[max_Pos_idx];

      for (int i = 0; i < cols; i++) {

        this.z_row[i] -= (z_multipiler * this.operation_Matrix[out_idx][i]);

      }

    }
    System.out.println("\n--- Optimal Solution Found ---");
    double optimal_z = 0.0;

    // Loop through your original variables (x1, x2, etc.)
    // We use the objective_function_arr size to ignore the Slack variables
    for (int j = 0; j < this.objective_function_arr.length; j++) {

      // Find the String name of the variable at column j (e.g., "x1")
      String var_name = "";
      for (Map.Entry<String, Integer> entry : this.variable_indeces.entrySet()) {
        if (entry.getValue() == j) {
          var_name = entry.getKey();
          break;
        }
      }

      // Check if this variable is currently a Basic Variable
      double final_value = 0.0;
      for (int i = 0; i < rows; i++) {
        if (this.basic_variables[i].equals(var_name)) {
          final_value = this.result_arr[i]; // Found it! Grab its RHS answer.
          break;
        }
      }

      // Print the variable's answer
      System.out.println(var_name + " = " + final_value);

      // Add its contribution to the total Profit (Z)
      // Note: We use the original objective array, so we don't worry about the
      // MIN/MAX flip here!
      optimal_z += (this.objective_function_arr[j] * final_value);
    }

    System.out.println("Optimal Z = " + optimal_z);

  }

}
