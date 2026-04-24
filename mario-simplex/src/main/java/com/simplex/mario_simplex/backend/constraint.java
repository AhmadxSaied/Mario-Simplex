package com.simplex.mario_simplex.backend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class constraint{
    private String constraint_to_be_parsed;
    private String constraint_type;
    private double bound_value; // == b
    private SimplexSolver simplex;
    private final Map<String,Boolean> used_variables;
    private final  Map<String,Boolean> unrestriced;

    public constraint(String constraint){
        this.constraint_to_be_parsed = constraint;
        this.used_variables = new HashMap<>();
        this.unrestriced = new HashMap<>();
    }

    private void  get_constraint_type() throws Exception{
        if(constraint_to_be_parsed.contains(">=")) this.constraint_type = ">=";
        else if(constraint_to_be_parsed.contains("<=")) this.constraint_type =  "<=";
        else if(constraint_to_be_parsed.contains("=")) this.constraint_type = "=";
        else{
            throw new Exception();
        }
    }

    protected Map<String,Double> parse_constraint()throws Exception{

        constraint_to_be_parsed = constraint_to_be_parsed.replaceAll(" ", "");
        String[] variable__b = constraint_to_be_parsed.split(constraint_type);
        this.bound_value = Double.parseDouble(variable__b[1]);  

        String[] variables = variable__b[0].split("\\+");
        
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)(x(\\d+))?");
        Matcher matcher;
        Map<String,Double> var_coef = new HashMap<>();
        for(String coefficient : variables){

            matcher = pattern.matcher(coefficient);

            if(matcher.find()){
                String key = matcher.group(2);
                if(matcher.group(1).equalsIgnoreCase("")){

                    if(unrestriced.containsKey(key)){
                        var_coef.put(key+"_0", 1.0);

                        var_coef.put(key+"_1", -1.0);
                    }else
                        var_coef.put(key, 1.0);
                }else{
                    if(unrestriced.containsKey(matcher.group(2))){
                        var_coef.put(key+"_0",
                         Double.parseDouble(matcher.group(1)) * 1.0
                        );
                        var_coef.put(key+"_1",Double.parseDouble(matcher.group(1)) * -1);
                    }else
                        var_coef.put(key,
                        Double.parseDouble(matcher.group(1)) * 1.0
                       );
                }
            }
        }
        System.out.println(var_coef);
        return var_coef;
    }

    protected void set_simplex(SimplexSolver simplex){
        this.simplex = simplex;
    }
    // returns the equation
    protected void load_used_variables() throws Exception{
        get_constraint_type();
        constraint_to_be_parsed = constraint_to_be_parsed.replaceAll(" ", "");
        String[] variable__b = constraint_to_be_parsed.split(constraint_type);
        this.bound_value = Double.parseDouble(variable__b[1]);  

        String[] variables = variable__b[0].split("\\+");
        
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)(x(\\d+))?"); 
        Matcher matcher;

        for(String coefficient : variables){
            matcher = pattern.matcher(coefficient);
            if(matcher.find()){
                this.used_variables.put(matcher.group(2),true);
            }
        }
    }


    protected List<Map<String,Double>> form_constraint_equations() throws Exception{
        
        load_used_variables();
        
        Map<String,String> variable_states = new HashMap<>(this.simplex.get_variable_inequalities());
        for(Map.Entry<String,String> entry : variable_states.entrySet()){
            String key = entry.getKey();
            if(used_variables.containsKey(key)){
                if(entry.getValue().equalsIgnoreCase("=")){

                    this.unrestriced.put(key, true);
                    this.used_variables.remove(key);

                    this.used_variables.put(key+"_0", true);
                    this.used_variables.put(key+"_1", true);

                }
            }
        }
        
        Map<String,Double> hashed_constraint = parse_constraint();
        hashed_constraint.put("b", bound_value);

        ArrayList<Map<String,Double>> result = new ArrayList<>(List.of(hashed_constraint));

        if(constraint_type.equalsIgnoreCase("=")){
            Map<String,Double> second_equation = new HashMap<>(hashed_constraint);
            
            for(Map.Entry<String,Double> entry : second_equation.entrySet()){
                second_equation.replace(entry.getKey(), entry.getValue()*-1);
            }
            result.add(second_equation);
        }
        if(constraint_type.equalsIgnoreCase(">=")){
            
            for(Map.Entry<String,Double> entry : hashed_constraint.entrySet()){
                hashed_constraint.replace(entry.getKey(), entry.getValue()*-1);
            }
            result.removeLast();
            result.add(hashed_constraint);
        }
        return result;

    }
}
