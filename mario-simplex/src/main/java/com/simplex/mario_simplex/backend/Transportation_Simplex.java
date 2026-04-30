package com.simplex.mario_simplex.backend;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Transportation_Simplex {
    private Integer[][] Operation_matrix;
    private Integer[][] solution;
    private Integer[] supply;
    private Integer[] demand;
    private Integer[] demand_clone;
    private Integer[] supply_clone;
    public Transportation_Simplex(Integer[][] Operation_matrix,Integer[] supply,Integer[] demand){
        assert (Operation_matrix.length == supply.length);
        assert (Operation_matrix[0].length == demand.length);
        this.Operation_matrix = Operation_matrix;
        this.supply = supply;
        this.demand = demand;
        this.demand_clone = demand.clone();
        this.supply_clone = supply.clone();
        this.solution = new Integer[Operation_matrix.length][Operation_matrix[0].length];
        for(Integer[] row : this.solution){
        Arrays.fill(row, 0);
        }
    }

    public void  NorthWest_initialization(){
        int i=0;
        int j=0;
        while(true){
            if(i>= this.supply.length || j >= this.demand.length) break;
            int min_demand_supply = Math.min(this.supply[i], this.demand[j]);
            this.supply[i] -=  min_demand_supply;
            this.demand[j] -= min_demand_supply;
            this.solution[i][j] = min_demand_supply;
            if(this.supply[i]>0){
                j++;
            }else{
                i++;
            }
        }
        int rows = this.solution.length;
        int cols = this.solution[0].length;
        
        // ── Header ────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println(  "║       Initial Feasible Solution      ║");
        System.out.println(  "╚══════════════════════════════════════╝\n");
        
        // Column numbers
        System.out.printf("%-10s", "");
        for (int x = 0; x < cols; x++)
            System.out.printf("  D%-4d", x + 1);
        System.out.printf("  %-8s%n", "Supply");
        System.out.println("─".repeat(10 + cols * 7 + 10));
        
        // Rows
        for (int y = 0; y < rows; y++) {
            System.out.printf("S%-9d", i + 1);
            for (int x = 0; x < cols; x++)
                System.out.printf("  %-5d", this.solution[y][x]);
            System.out.printf("  %-8d%n", this.supply_clone[y]);
        }
        
        System.out.println("─".repeat(10 + cols * 7 + 10));
        
        // Demand row
        System.out.printf("%-10s", "Demand");
        for (int x = 0; x < cols; x++)
            System.out.printf("  %-5d", this.demand_clone[x]);
        System.out.println();
        
    }
    private void solve_u_v(Integer[]u , Integer[]v){
        System.out.println();
        boolean changed = true;
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println(  "║            U_V Calculation           ║");
        System.out.println(  "╚══════════════════════════════════════╝\n");
        while (changed) { 
            changed = false;
            for (int i = 0; i < this.solution.length; i++) {
                for (int j = 0; j < this.solution[0].length; j++) {
                    if (this.solution[i][j] > 0) {
            
                        if (u[i] != null && v[j] == null) {
                            v[j] = this.Operation_matrix[i][j] - u[i];
                            System.out.printf("  v[%d] = C(%d,%d) - u[%d]  →  v[%d] = %d - (%d) = %d%n",
                                    j+1, i+1, j+1, i+1,
                                    j+1, this.Operation_matrix[i][j], u[i], v[j]);
                            changed = true;
            
                        } else if (v[j] != null && u[i] == null) {
                            u[i] = this.Operation_matrix[i][j] - v[j];
                            System.out.printf("  u[%d] = C(%d,%d) - v[%d]  →  u[%d] = %d - (%d) = %d%n",
                                    i+1, i+1, j+1, j+1,
                                    i+1, this.Operation_matrix[i][j], v[j], u[i]);
                            changed = true;
                        }
            
                    }
                }
            }
        }
    }
    public void solve(){
        while (true) { 
            
        
        Integer [] U = new Integer[this.solution.length];
        Integer [] V = new Integer[this.solution[0].length];
        U[0] = 0;
        solve_u_v(U, V);
        int min_i = -1;
        int min_j = -1;
        int current_Min = Integer.MAX_VALUE;
        for(int i=0;i<this.solution.length;i++){
            for(int j=0;j<this.solution[0].length;j++){
                if(this.solution[i][j]==0){
                    int shadow_cost = this.Operation_matrix[i][j] - U[i] - V[j];
                    if(current_Min > shadow_cost){
                        min_i = i;
                        min_j = j;
                        current_Min = shadow_cost;
                    }
                }
            }
        }
        if(current_Min >=0) break;
        List<Map.Entry<Integer,Integer>> loop = getLoop(min_i, min_j);
        int factor = Integer.MAX_VALUE;
        for(int i=1;i<loop.size();i+=2){
            int val = this.solution[loop.get(i).getKey()][loop.get(i).getValue()];
            if(factor > val){
                factor = val;
            }
        }

        for(int k = 0; k<loop.size();k++){
            int r = loop.get(k).getKey();
            int c = loop.get(k).getValue();
            if(k%2 ==0){
                this.solution[r][c] +=factor;
            }else{
                this.solution[r][c] -= factor;
            }
        }
        int rows = this.solution.length;
        int cols = this.solution[0].length;
        
        // ── Header ────────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println(  "║         TRANSPORTATION SOLUTION      ║");
        System.out.println(  "╚══════════════════════════════════════╝\n");
        
        // Column numbers
        System.out.printf("%-10s", "");
        for (int j = 0; j < cols; j++)
            System.out.printf("  D%-4d", j + 1);
        System.out.printf("  %-8s%n", "Supply");
        System.out.println("─".repeat(10 + cols * 7 + 10));
        
        // Rows
        for (int i = 0; i < rows; i++) {
            System.out.printf("S%-9d", i + 1);
            for (int j = 0; j < cols; j++)
                System.out.printf("  %-5d", this.solution[i][j]);
            System.out.printf("  %-8d%n", this.supply_clone[i]);
        }
        
        System.out.println("─".repeat(10 + cols * 7 + 10));
        
        // Demand row
        System.out.printf("%-10s", "Demand");
        for (int j = 0; j < cols; j++)
            System.out.printf("  %-5d", this.demand_clone[j]);
        System.out.println();
        
        
    }
    int final_cost = 0;

    int rows = this.solution.length;
    int cols = this.solution[0].length;
    
    // Calculate final cost
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            if (this.solution[i][j] != 0)
                final_cost += this.Operation_matrix[i][j] * this.solution[i][j];
    
    // ── Result ────────────────────────────────────────────────
    System.out.println("\n╔══════════════════════════════════════╗");
    System.out.printf( "║  %-36s║%n", "OPTIMAL Z = " + final_cost);
    System.out.println("╚══════════════════════════════════════╝\n");
    }   

    
    private boolean findPath(int currR,int currC,boolean moveHorizontal,List<Map.Entry<Integer,Integer>> path,int startR,int startC){
        if(moveHorizontal){
            for(int nextC=0;nextC < this.solution[0].length;nextC++){
                if(nextC == currC) continue;
                if(nextC == startC && currR == startR && path.size() >= 4) return true;
                if(this.solution[currR][nextC] > 0){
                    path.add(Map.entry(currR, nextC));
                    if(findPath(currR, nextC, false,path , startR, startC)) return true;
                    path.removeLast();
                }
            }
        }else{
            for(int nextR=0 ; nextR < this.solution.length;nextR++){
                if(nextR == currR) continue;
                if(currC == startC && nextR == startR && path.size() >= 4) return true;
                if(this.solution[nextR][currC] > 0){
                    path.add(Map.entry(nextR, currC));
                    if(findPath(nextR, currC, true, path, startR, startC)) return true;
                    path.removeLast();
                }
            }
        }
        return false;
    }
    private List<Map.Entry<Integer,Integer>> getLoop(int startR,int startC){
        List<Map.Entry<Integer,Integer>> path = new ArrayList<>();
        path.add(Map.entry(startR, startC));
        if(findPath(startR, startC, true, path, startR, startC)){
            return path;
        }
        if(findPath(startR, startC, false, path, startR, startC)){
            return path;
        }
        return null;
    }
}
