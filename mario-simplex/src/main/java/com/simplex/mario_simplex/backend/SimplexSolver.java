package com.simplex.mario_simplex.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimplexSolver {
  // we will be trying to build a simplex method calculator for the Operation
  // research Lab
  // Ideas
  // the input format will be as follows
  // Matrix of Constraints m x n , m--> number of constraints, n--> number of
  // variables
  // vector of results b 1 x m , carrying the solution for each constraints

  // z --> size and A matrix size will depend on the variables
  // 1--- if there is an unrestricted variable Xo we will swap it with Xo_1 and
  // Xo_2 such that Xo_1 and Xo_2 >=0
  // 2--- we read in every contraint in m constraints if we find <= then we are OK
  // if we find >= we multiple all by -1
  // if we fint = we swap it with 2 constraints >= and <=
  // which will be <= * -1 & <=
  // i think in every constraint we will need a refrence to the variable
  // restriction so that
  // when we encounter >=0 we continue normally and when we find unrestricted we
  // swap it with 2 inside the constraint
  // we will then need to form the matrix that includes the slacks

  private enum Type {
    MAX,
    MIN
  }

  private enum Method {
    twoPhase,
    standard
  }

  protected Map<String, String> variable_inequalities;
  protected Map<String, Integer> variable_indeces;
  protected List<String> artifical_variables = new ArrayList<>();
  protected int S = 0;
  protected int E = 0;
  protected int A = 0;
  protected  ArrayList<constraint> constraints;
  protected Type problem_Type;
  protected Method method;
  protected double[][] operation_Matrix;
  private String objective_function;
  protected double[] objective_function_arr;
  protected double[] result_arr;
  private Set<String> unrestricted_token;
  protected String[] basic_variables;
  protected double[] z_row;

  public SimplexSolver(String variable_inequalities) {
    this.constraints = new ArrayList<>();
    this.variable_inequalities = new HashMap<>();
    this.problem_Type = Type.MAX;
    this.method = Method.standard;
    this.unrestricted_token = new HashSet<>();
    this.variable_indeces = new HashMap<>();
    parse_variable_inequalities(variable_inequalities);
  }
  public SimplexSolver() {
  }
  protected StandardSimplexSolver get_child(double[] objFunction,double[] z_row){
      StandardSimplexSolver child_solver = new StandardSimplexSolver();
      child_solver.operation_Matrix = this.operation_Matrix;
      child_solver.basic_variables = this.basic_variables;
      child_solver.result_arr = this.result_arr;
      child_solver.z_row = z_row;
      child_solver.variable_indeces = this.variable_indeces;
      child_solver.objective_function_arr = objFunction;
      child_solver.constraints = this.constraints;
      return child_solver;
  }


  public void addConstraint(constraint constrain) {
    this.constraints.add(constrain);
    constrain.set_simplex(this);
  }

  public void show_constraints() throws Exception {
    for (constraint x : constraints) {
      System.out.println(x.form_constraint_equations());
    }
    System.out.println(this.variable_inequalities);
  }

  public void objective_function(String ObjFunction, boolean type) {
    this.objective_function = ObjFunction;
    if (!type)
      this.problem_Type = Type.MIN;
    parse_objective_function();
  }

  protected Map<String, String> get_variable_inequalities() {
    return this.variable_inequalities;
  }

  private void parse_variable_inequalities(String var_inequalities) {
    var_inequalities = var_inequalities.replaceAll(" ", "");
    String[] splitted_details = var_inequalities.split(",");
    Pattern pattern = Pattern.compile("^(x\\d+)(=|>=|<=)(\\d+)$");
    Matcher matcher;
    for (String var : splitted_details) {
      matcher = pattern.matcher(var);
      if (matcher.find()) {
        this.variable_inequalities.put(matcher.group(1), matcher.group(2));
      }
    }
    System.out.println(this.variable_inequalities);
  }

  private void parse_objective_function() {
    this.objective_function = this.objective_function.replaceAll(" ", "");
    Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)(x(\\d+))?");
    Matcher matcher;
    HashMap<String, String> temp = new HashMap<>(this.variable_inequalities);
    int number_of_variables = 0;
    for (Map.Entry<String, String> entry : temp.entrySet()) {
      String Key = entry.getKey();
      String inequality = entry.getValue();

      if (inequality.equalsIgnoreCase("=")) {
        number_of_variables++;
        this.variable_inequalities.remove(Key);
        this.variable_inequalities.put(Key + "_0", ">=");
        this.variable_inequalities.put(Key + "_1", ">=");
        unrestricted_token.add(Key);
      }
      number_of_variables++;
    }

    String[] splitted_objective = this.objective_function.split("\\+");
    this.objective_function_arr = new double[number_of_variables];
    int offset = 0;
    for (String var : splitted_objective) {
      matcher = pattern.matcher(var);
      if (matcher.find()) {
        int initial_index = Integer.parseInt(matcher.group(3));

        if (unrestricted_token.contains(matcher.group(2))) {
          this.objective_function_arr[initial_index - 1 + offset] = Double.parseDouble(matcher.group(1));
          variable_indeces.put(matcher.group(2) + "_0", initial_index - 1 + offset);
          this.objective_function_arr[initial_index + offset] = Double.parseDouble(matcher.group(1)) * -1.0;
          variable_indeces.put(matcher.group(2) + "_1", initial_index + offset);
          offset++;
        } else {
          this.objective_function_arr[initial_index - 1 + offset] = Double.parseDouble(matcher.group(1));
          variable_indeces.put(matcher.group(2), initial_index - 1 + offset);
        }
      }
    }
    for (double i : this.objective_function_arr) {
      System.out.println(i);
    }

  }

  public void show_sorted() throws Exception {
    this.variable_inequalities = new TreeMap<>(this.variable_inequalities);
    System.out.println(this.variable_inequalities);
    for (constraint i : constraints) {
      System.out.println(i.form_constraint_equations());
    }
  }

  public void matrix_form() throws Exception {
    ArrayList<Map<String, Double>> rows = new ArrayList<>();

    for (constraint i : constraints) {
      Map<String, Double> holder = i.f_hashed_constraint;
      rows.add(holder);
    }
    int constrains = rows.size(); // m
    this.basic_variables = new String[constrains];
    int idx = 0;
    for (Map<String, Double> row : rows) {
      for (String key : row.keySet()) {
        if (key.equals("b")) {
          continue;
        }
        if (!variable_indeces.containsKey(key)) {
          variable_indeces.put(key, variable_indeces.size());
        }
        if (key.startsWith("S_") || key.startsWith("A_")) {
          this.basic_variables[idx] = key;

        }
      }
      idx++;
    }
    System.out.println("vairable indeces");
    System.out.println(this.variable_indeces);
    int variables = this.variable_indeces.size(); // n
    this.z_row = new double[variables];
    for (int i = 0; i < this.objective_function_arr.length; i++) {
      double coeff = this.objective_function_arr[i];
      if (this.problem_Type == Type.MIN) {
        coeff = coeff * -1;
      }
      this.z_row[i] = coeff;
    }
    // need to add a z_row for the 2phase method
    this.operation_Matrix = new double[constrains][variables];
    this.result_arr = new double[constrains];
    int index = 0;
    for (Map<String, Double> row : rows) {

      for (Map.Entry<String, Double> entry : row.entrySet()) {
        String key = entry.getKey();
        Double value = entry.getValue();

        if (!key.equalsIgnoreCase("b")) {
          int inner_index = this.variable_indeces.get(key);
          this.operation_Matrix[index][inner_index] = value;
        } else
          this.result_arr[index] = value;
      }
      index++;
    }
    for (double[] r : this.operation_Matrix) {
      for (double i : r) {
        System.out.print(i + " ");
      }
      System.out.println();
    }
    for (double i : this.result_arr) {
      System.out.print(i + " ");
    }
  }

  protected String getNewArtificalID() {
    this.method = Method.twoPhase;
    String var = "A_" + Integer.toString(this.A);
    this.A++;
    this.artifical_variables.add(var);
    return var;

  }

  protected String getNewSurPlusID() {
    return "E_" + Integer.toString(this.A++);
  }

  protected String getNewSlackID() {
    return "S_" + Integer.toString(this.S++);
  }

  public double[][] getOperation_Matrix() {
    return this.operation_Matrix;
  }
  public double[][] solve()throws  Exception{
    /*
    if we have artificial variables we make an instanse of standard solver
    MIN z = Artificial
    double[][] -->  see basic variables and zero out the below Z
    // modify stanrdard solver to solve the new double[][]
    */
   if(this.method == Method.twoPhase){
   double[] minimize_phase_one = new double[this.variable_indeces.size()];
   Arrays.fill(minimize_phase_one, 0);
    int artifical_variables_count =0;
   for(Map.Entry<String,Integer> entry : this.variable_indeces.entrySet()){
      String key = entry.getKey();
      Integer value = entry.getValue();
      if(key.contains("A")){
        minimize_phase_one[value] = -1;
        artifical_variables_count++;
      }
   }
   StandardSimplexSolver child_solver = get_child(minimize_phase_one,minimize_phase_one);
   child_solver.solveStandard();
   double[][] result_mat = child_solver.operation_Matrix;
   for(double[] x: result_mat){
    for(double i : x){
      System.out.print(i+" ");
    }
    System.out.println();
   }
   double[] result_vec = child_solver.result_arr; 
   for(double i : result_vec){
    System.out.print(i+" ");
   }
   int rows = this.constraints.size();
   int columns = this.variable_indeces.size();
    double[][] phase_two_matrix = new double[rows][columns];

    for(Map.Entry<String,Integer> entry : this.variable_indeces.entrySet()){
      String key = entry.getKey();
      Integer index = entry.getValue();

      if(key.contains("A")) continue;
      for(int i=0;i<rows;i++){
        phase_two_matrix[i][index] = result_mat[i][index];
      }
    }
    System.out.println();
    for(double[] x: phase_two_matrix){
      for(double i : x){
        System.out.print(i+" ");
      }
      System.out.println();
     }
     child_solver.z_row = this.z_row;
     child_solver.operation_Matrix = phase_two_matrix;
     child_solver.objective_function_arr = this.objective_function_arr;
     child_solver.solveStandard();
     double[][] final_mat = child_solver.operation_Matrix;
     for(double[] x: final_mat){
      for(double i : x){
        System.out.print(i+" ");
      }
      System.out.println();
     }
  }
  return null;
  } 
}
