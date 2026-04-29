import { Injectable, signal } from '@angular/core';

// This interface exactly matches what the user types into your form
export interface SimplexInputState {
  numVars: number;
  objectiveType: 'MAX' | 'MIN';
  objectiveParams: number[]; // e.g., [0.75, -20, 0.5, -6]
  constraintParams:{coefs: number[]; sign: string; rhs: number }[];
  constraintSigns: string[]; // e.g., ['<=', '<=', '>=']
}

@Injectable({
  providedIn: 'root', // This ensures the data lives forever while the app is open
})
export class SimplexStateService {
  // We use a Signal so your components can easily react to it
  savedInput = signal<SimplexInputState | null>(null);

  saveState(state: SimplexInputState) {
    this.savedInput.set(state);
  }

  clearState() {
    this.savedInput.set(null);
  }
}
