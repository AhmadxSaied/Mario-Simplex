export interface SimplexResults {
  VariableNames: string[];
  Matrix: number[][];
  Basic_Variables: string[];
  b_row: number[];
  PivotRow: number;
  PivotCol: number;
  RatioResults: number[];
  z_row: number[];
  Type: "Phase 1" | "Phase 2" | null;
  State?: "OPTIMAL" | "INFEASIBLE" | "INFINITE_SOLUTION"
  | "DEGENERATE_SOLUTION" | "UNBOUNDED_SOLUTION" | null;
}
