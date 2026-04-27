export interface SimplexResults {
  VariableNames: string[];
  Matrix: number[][];
  Basic_Variables: string[];
  b_row: number[];
  PivotRow: number;
  PivotCol: number;
  RatioResults: number[];
}
export const MOCK_RESULTS: SimplexResults[] = [{
  VariableNames: ['x1', 'x2', 'x3', 's1', 's2'],
  Matrix: [
    [1, 4, 5, 1, 0],
    [20, 9, 10, 0, 1]
  ],
  Basic_Variables: ['A0', 'S0'],
  b_row: [8, 0],
  PivotRow: 1,
  PivotCol: 0,
  RatioResults: [10, 8, 5]
}, {
  VariableNames: ['x1', 'x2', 'x3', 's1', 's2'],
  Matrix: [
    [1, 4, 5, 1, 0],
    [20, 9, 10, 0, 1]
  ],
  Basic_Variables: ['A0', 'S0'],
  b_row: [8, 0],
  PivotRow: 1,
  PivotCol: 0,
  RatioResults: [10, 8, 5]
}];
