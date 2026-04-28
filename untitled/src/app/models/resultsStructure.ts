export interface SimplexResults {
  VariableNames: string[];
  Matrix: number[][];
  Basic_Variables: string[];
  b_row: number[];
  PivotRow: number;
  PivotCol: number;
  RatioResults: number[];
  z_row: number[];
  Type:"Phase 1" | "Phase 2" | "Standard" | null;
  state?: "OPTIMAL" | "INFEASIBLE" | "INFINITE_SOLUTION" 
  | "DEGENERATE_SOLUTION" | "UNBOUNDED_SOLUTION" | null;
}
export const MOCK_RESULTS: SimplexResults[] = [
  {
    VariableNames: ['x1', 'x2', 's1', 's2'],
    Basic_Variables: ['s1', 's2'],
    Matrix: [
      [1, 1, 1, 0], // s1 row coefficients
      [2, 1, 0, 1], // s2 row coefficients
    ],
    b_row: [4, 5], // Right Hand Side (RHS)
    z_row: [-3, -2, 0, 0, 0], // The Objective Row (Z value is the last 0)
    PivotCol: 0, // Most negative in z_row is -3 (index 0)
    RatioResults: [4, 2.5], // 4/1 = 4, 5/2 = 2.5
    PivotRow: 1, // Smallest ratio is 2.5 (index 1)
    Type: 'Phase 2',
  },

  // ITERATION 1 (Progress: 50%) - First Pivot Completed
  {
    VariableNames: ['x1', 'x2', 's1', 's2'],
    Basic_Variables: ['s1', 'x1'], // x1 enters, s2 leaves
    Matrix: [
      [0, 0.5, 1, -0.5],
      [1, 0.5, 0, 0.5],
    ],
    b_row: [1.5, 2.5],
    z_row: [0, -0.5, 0, 1.5, 7.5], // Current Z = 7.5
    PivotCol: 1, // Most negative is -0.5 (index 1)
    RatioResults: [3, 5], // 1.5/0.5 = 3, 2.5/0.5 = 5
    PivotRow: 0, // Smallest ratio is 3 (index 0)
    Type: 'Phase 2',
  },

  // ITERATION 2 (Progress: 100%) - Optimal Solution Reached
  {
    VariableNames: ['x1', 'x2', 's1', 's2'],
    Basic_Variables: ['x2', 'x1'], // x2 enters, s1 leaves
    Matrix: [
      [0, 1, 2, -1],
      [1, 0, -1, 1],
    ],
    b_row: [3, 1], // Final values: x2 = 3, x1 = 1
    z_row: [0, 0, 1, 1, 9], // Final Z = 9 (No negatives left!)
    PivotCol: -1, // -1 means no pivot column (Optimal!)
    RatioResults: [],
    PivotRow: -1, // -1 means no pivot row (Optimal!)
    Type: 'Phase 2',
  },
];
