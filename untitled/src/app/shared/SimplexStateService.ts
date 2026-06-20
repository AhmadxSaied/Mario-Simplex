import { Injectable, signal } from '@angular/core';

export interface SimplexInputState {
  numVars: number;
  objectiveType: 'MAX' | 'MIN';
  objectiveParams: number[];
  constraintParams:{coefs: number[]; sign: string; rhs: number }[];
  constraintSigns: string[];
}

@Injectable({
  providedIn: 'root',
})
export class SimplexStateService {
  savedInput = signal<SimplexInputState | null>(null);

  saveState(state: SimplexInputState) {
    this.savedInput.set(state);
  }

  clearState() {
    this.savedInput.set(null);
  }
}
