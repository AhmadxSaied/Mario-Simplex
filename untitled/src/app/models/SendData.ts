export interface BackendData {
  objectiveType: "MAX" | "MIN";
  objectiveFunction: string;
  constraints: string[];
}
