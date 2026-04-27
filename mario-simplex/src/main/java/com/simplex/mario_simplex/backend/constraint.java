package com.simplex.mario_simplex.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class constraint {
  private String constraint_to_be_parsed; // input constraint that will be received from the user
  private String constraint_type; // Reveals the type meaning is is smaller than or equal | equal | greater than
                                  // or equal
  private double bound_value; // == b
  private SimplexSolver simplex; // referece to the parent solver that the constraint is added to as we will need
                                 // some properties from the parent about the problem
  private final Map<String, Boolean> used_variables; // holds the variables used in this constraint which can be a
                                                     // subset of the whole variables
  private final Map<String, Boolean> unrestriced; // hold the property about a constraint that says if the variable is
                                                  // >= or it is unrestricted | holds only unresricted variables
  protected Map<String, Double> f_hashed_constraint; // stores the variable and its coeficient so it is easily read
                                                     // inside the simplex solver

  public constraint(String constraint) {
    this.constraint_to_be_parsed = constraint;
    this.used_variables = new HashMap<>();
    this.unrestriced = new HashMap<>();
  }

  // tells us what type of problem are we dealing with
  private void get_constraint_type() throws Exception {
    if (constraint_to_be_parsed.contains(">="))
      this.constraint_type = ">=";
    else if (constraint_to_be_parsed.contains("<="))
      this.constraint_type = "<=";
    else if (constraint_to_be_parsed.contains("="))
      this.constraint_type = "=";
    else {
      throw new Exception();
    }
  }

  // here we parse the input constraint into chuncks of name and coeffecient that
  // can be dealt with inside the solver
  protected Map<String, Double> parse_constraint() throws Exception {
    // we remove all white spaces from the string
    constraint_to_be_parsed = constraint_to_be_parsed.replaceAll(" ", "");
    // we split the constraint into two tokens the equation and the value it is
    // equal to (aka B)
    String[] variable__b = constraint_to_be_parsed.split(constraint_type);
    // second token is always the B
    this.bound_value = Double.parseDouble(variable__b[1]);

    // we break down the function into further tokens splitted by +
    // an assumption is made that the constraint is in the shape of
    // x1 + -x2 + -x3 // if coeffecient is negative it is written as (+ -aX)
    String[] variables = variable__b[0].split("\\+");

    // the pattern we will match
    // group 1 will be the coeffecient itself ([+-]?\\d*\\.?\\d*)
    // group 2 will be the name of the variable (x(\\d+))?
    // group 3 will be the index of the variable (the 2 in X2)
    Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)(x(\\d+))?");
    Matcher matcher;
    Map<String, Double> var_coef = new HashMap<>();
    for (String coefficient : variables) {

      matcher = pattern.matcher(coefficient);
      // if we find a match we proceed
      // we must use matcher.find() so matcher can process the data
      if (matcher.find()) {
        String key = matcher.group(2); // the name of the variable
        if (matcher.group(1).equalsIgnoreCase("")) { // the case that the coeffecient is 1

          // the case that the variable is unrestricted
          if (unrestriced.containsKey(key)) {
            var_coef.put(key + "_0", 1.0);

            var_coef.put(key + "_1", -1.0);
          } else
            var_coef.put(key, 1.0);
        } else {
          // case that coeffecient is not 1
          if (unrestriced.containsKey(matcher.group(2))) {
            var_coef.put(key + "_0",
                Double.parseDouble(matcher.group(1)) * 1.0);
            var_coef.put(key + "_1", Double.parseDouble(matcher.group(1)) * -1);
          } else
            var_coef.put(key,
                Double.parseDouble(matcher.group(1)) * 1.0);
        }
      }
    }
    // we return the map containing the variable and coeffiecent pair
    System.out.println(var_coef);
    return var_coef;
  }

  // we set the solver
  protected void set_simplex(SimplexSolver simplex) {
    this.simplex = simplex;
  }

  // loading the constraint variables
  protected void load_used_variables() throws Exception {
    // first we see what type we are
    get_constraint_type();
    // we so the splitting part as before
    constraint_to_be_parsed = constraint_to_be_parsed.replaceAll(" ", "");
    String[] variable__b = constraint_to_be_parsed.split(constraint_type);
    this.bound_value = Double.parseDouble(variable__b[1]);

    String[] variables = variable__b[0].split("\\+");

    Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)(x(\\d+))?");
    Matcher matcher;
    // this is used to tell us what variables are present in the constraint
    for (String coefficient : variables) {
      matcher = pattern.matcher(coefficient);
      if (matcher.find()) {
        this.used_variables.put(matcher.group(2), true);
      }
    }
  }

  // forms the constraint coeffecient map and returns the full mapping with the
  // unrestricted variables into consideration
  protected Map<String, Double> form_constraint_equations() throws Exception {

    load_used_variables();

    Map<String, String> variable_states = new HashMap<>(this.simplex.get_variable_inequalities()); // gets the variables
                                                                                                   // statues >= or
                                                                                                   // unrestricted
    // this swaps any unrestricted variable Xn with Xn_0 and Xn_1
    for (Map.Entry<String, String> entry : variable_states.entrySet()) {
      String key = entry.getKey();
      if (used_variables.containsKey(key)) {
        if (entry.getValue().equalsIgnoreCase("=")) {

          this.unrestriced.put(key, true);
          this.used_variables.remove(key);

          this.used_variables.put(key + "_0", true);
          this.used_variables.put(key + "_1", true);

        }
      }
    }

    Map<String, Double> hashed_constraint = parse_constraint(); // returns the parsed constraints
    hashed_constraint.put("b", bound_value);
    // here we perform the slack | surplus and artificial variable addition based on
    // our problem type
    if (constraint_type.equalsIgnoreCase("<=")) {
      String sVar = simplex.getNewSlackID();
      hashed_constraint.put(sVar, 1.0);
    } else if (constraint_type.equalsIgnoreCase(">=")) {
      String aVar = simplex.getNewArtificalID();
      String eVar = simplex.getNewSurPlusID();
      hashed_constraint.put(eVar, -1.0);
      hashed_constraint.put(aVar, 1.0);
    } else if (constraint_type.equalsIgnoreCase("=")) {
      String aVar = simplex.getNewArtificalID();
      hashed_constraint.put(aVar, 1.0);
    }
    this.f_hashed_constraint = hashed_constraint;
    // we return the fully tokenized constraint
    return hashed_constraint;

  }
}
