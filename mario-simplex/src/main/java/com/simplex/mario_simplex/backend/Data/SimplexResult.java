package com.simplex.mario_simplex.backend.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimplexResult {

    @JsonProperty("VariableNames")
    private String[] variableNames;

    @JsonProperty("Matrix")
    private double[][] matrix;

    @JsonProperty("Basic_Variables")
    private String[] basicVariables;

    @JsonProperty("b_row")
    private double[] bRow;

    @JsonProperty("PivotRow")
    private int pivotRow;

    @JsonProperty("PivotCol")
    private int pivotCol;

    @JsonProperty("RatioResults")
    private double[] ratioResults;

    @JsonProperty("z_row")
    private double[] z_row;

    @JsonProperty("Type")
    private String Type;

    public SimplexResult() {
    }

    public SimplexResult(String[] variableNames, double[][] matrix, String[] basicVariables,
            double[] bRow, int pivotRow, int pivotCol, double[] ratioResults,double[] z_row,String Type) {
        this.variableNames = variableNames;
        this.matrix = matrix;
        this.basicVariables = basicVariables;
        this.bRow = bRow;
        this.pivotRow = pivotRow;
        this.pivotCol = pivotCol;
        this.ratioResults = ratioResults;
        this.Type = Type;
        this.z_row = z_row;
    }

    // Getters and Setters
    public void setVariableNames(String[] variableNames) {
        this.variableNames = variableNames;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public void setBasicVariables(String[] basicVariables) {
        this.basicVariables = basicVariables;
    }

    public void setBRow(double[] bRow) {
        this.bRow = bRow;
    }

    public void setPivotRow(int pivotRow) {
        this.pivotRow = pivotRow;
    }

    public void setPivotCol(int pivotCol) {
        this.pivotCol = pivotCol;
    }

    public void setRatioResults(double[] ratioResults) {
        this.ratioResults = ratioResults;
    }

    public String[] getVariableNames() {
        return variableNames;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public String[] getBasicVariables() {
        return basicVariables;
    }

    public double[] getBRow() {
        return bRow;
    }

    public int getPivotRow() {
        return pivotRow;
    }

    public int getPivotCol() {
        return pivotCol;
    }

    public double[] getRatioResults() {
        return ratioResults;
    }

}
