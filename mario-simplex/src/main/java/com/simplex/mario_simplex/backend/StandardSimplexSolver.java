package com.simplex.mario_simplex.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.simplex.mario_simplex.backend.Data.SimplexResult;

public class StandardSimplexSolver extends SimplexSolver {

    // Constructor
    public StandardSimplexSolver(String conditions) throws Exception {

        // This calls the parent SimplexSolver constructor first!
        super(conditions);

    }

    public StandardSimplexSolver() {

    }

    public List<SimplexResult> solveStandard() {

        List<SimplexResult> results = new ArrayList<>();

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
                // this calculates the summation of Ci * aji for the basic variable
                // this is done to insure that the objective function where basic variables
                // columns is are zeroed out
                zj += cb * this.operation_Matrix[j][i];

            }
            // calculates the Z which will go start the simplex algorithm
            cj_zj[i] = this.z_row[i] - zj;

        }

        this.z_row = cj_zj;

        while (true) {
            // we find the maximum columns that has the largest Z factor and that will be
            // our entering variable
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
            double[] ratio_results = new double[rows];

            double out_value = Integer.MAX_VALUE;
            // this loop is for carrying out the ratio test and determine the smallest ratio
            // as our leaving variable
            for (int i = 0; i < rows; i++) {

                double pivot_elemnt = this.operation_Matrix[i][max_Pos_idx];

                if (pivot_elemnt > 0) {
                    // we keep track of the smallest ratio and its index
                    double ratio = this.result_arr[i] / pivot_elemnt;
                    ratio_results[i] = ratio;
                    if (ratio < out_value) {

                        out_idx = i;

                        out_value = ratio;

                    }

                }

            }
            // check that all ratios are equal
            this.state = "DEGENERATE_SOLUTION";
            for(int i = 0 ; i<rows;i++){
                if(!(ratio_results[i] == out_value)){
                    this.state = "OPTIMAL"; break;
                }
            }
            // if all pivot elements are zeros we throw an exception
            if (out_value == Integer.MAX_VALUE) {
                // throw Exception
                this.state = "UNBOUNDED_SOLUTION";
                System.out.println(this.state);
                break;

            }
            double[][] clonedMatrix = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                clonedMatrix[i] = this.operation_Matrix[i].clone();
            }
            // Calculate current Z for the snapshot
            double current_z = 0.0;
            for (int j = 0; j < this.objective_function_arr.length; j++) {
                String var_name = "";
                for (Map.Entry<String, Integer> entry : this.variable_indeces.entrySet()) {
                    if (entry.getValue() == j) { var_name = entry.getKey(); break; }
                }
                double final_value = 0.0;
                for (int i = 0; i < rows; i++) {
                    if (this.basic_variables[i].equals(var_name)) {
                        final_value = this.result_arr[i]; break;
                    }
                }
                current_z += (this.objective_function_arr[j] * final_value);
            }

            double[] z = Arrays.copyOf(this.z_row, this.z_row.length + 1);
            z[z.length - 1] = current_z;
            String[] variableNames = new String[this.variable_indeces.size()];
            for (Map.Entry<String, Integer> entry : this.variable_indeces.entrySet()) {
                variableNames[entry.getValue()] = entry.getKey();
            }

            // ADD SNAPSHOT TO RESULTS
            results.add(new SimplexResult(
                    variableNames.clone(),
                    clonedMatrix,
                    this.basic_variables.clone(),
                    this.result_arr.clone(),
                    out_idx,
                    max_Pos_idx,
                    ratio_results.clone(),
                    z.clone(),
                    this.phase,
                    this.state)
            );

            // swap base
            String entering_var_name = "";
            // we get the name of the of the entering variable
            for (Map.Entry<String, Integer> entry : this.variable_indeces.entrySet()) {

                if (entry.getValue() == max_Pos_idx) {

                    entering_var_name = entry.getKey();

                    break;

                }

            }

            this.basic_variables[out_idx] = entering_var_name;

            // divide the matrix values and the result arr
            // we normalize the row which will carry the gauss jordon
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
                // gauss jordon step
                for (int j = 0; j < cols; j++) {

                    this.operation_Matrix[i][j] -= (multipiler * this.operation_Matrix[out_idx][j]);

                }

                this.result_arr[i] -= (multipiler * this.result_arr[out_idx]);

            }
            // note that we only need the multiplier as multiply the row with it as the row
            // is already normalized
            double z_multipiler = this.z_row[max_Pos_idx];

            for (int i = 0; i < cols; i++) {

                this.z_row[i] -= (z_multipiler * this.operation_Matrix[out_idx][i]);

            }
        }
        System.out.println("\n--- Optimal Solution Found ---");
        double optimal_z = 0.0;
        String[] variableNames = new String[this.variable_indeces.size()];
        for(Map.Entry<String,Integer> entry : this.variable_indeces.entrySet()){
            variableNames[entry.getValue()] = entry.getKey();
        }
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
            // the below line calculates the result from the Z array as detailed the
            // contributed to total profit
            // this helps us as we are no longer worry when to multiply z with -1 and when
            // to leave it as it is
            // SMART ZUHAIR

            // Add its contribution to the total Profit (Z)
            // Note: We use the original objective array, so we don't worry about the
            // MIN/MAX flip here!
            optimal_z += (this.objective_function_arr[j] * final_value);
        }

        System.out.println("Optimal Z = " + optimal_z);
        double[] final_z = Arrays.copyOf(this.z_row, this.z_row.length + 1);
        final_z[final_z.length - 1] = optimal_z;
        results.add(new SimplexResult(
                variableNames.clone(),
                this.operation_Matrix.clone(), // Use the deep copied matrix
                this.basic_variables.clone(),
                this.result_arr.clone(),
                -1, // No more math!
                -1, // No more math!
                new double[0], // Empty ratios
                final_z.clone(), // Now it exists!
                this.phase,
                this.state
        ));
        System.out.println(this.phase);
        return results;
    }

}
