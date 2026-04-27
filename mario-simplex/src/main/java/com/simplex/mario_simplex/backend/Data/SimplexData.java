package com.simplex.mario_simplex.backend.Data;

public class SimplexData {

    private String objectiveType; // "max" or "min"
    private String objectiveFunction;
    private String[] constraints;
    private String[] bounds;

    public SimplexData() {

    }

    public SimplexData(String[] bounds, String[] constraints, String objectiveFunction, String objectiveType) {
        this.bounds = bounds;
        this.constraints = constraints;
        this.objectiveFunction = objectiveFunction;
        this.objectiveType = objectiveType;
    }

    // Getters and setters
    public void setObjectiveType(String objectiveType) {
        this.objectiveType = objectiveType;
    }

    public void setObjectiveFunction(String objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }

    public void setBounds(String[] bounds) {
        this.bounds = bounds;
    }

    public String getObjectiveType() {
        return objectiveType;
    }

    public String getObjectiveFunction() {
        return objectiveFunction;
    }

    public String[] getConstraints() {
        return constraints;
    }

    public String[] getBounds() {
        return bounds;
    }
}
