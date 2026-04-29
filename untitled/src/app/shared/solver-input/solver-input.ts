import { computed, Component, signal, inject } from '@angular/core';
import { BackendData } from '../../models/SendData';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Results } from '../../services/Results';

@Component({
  selector: 'app-solver-input',
  imports: [CommonModule, FormsModule],
  templateUrl: './solver-input.html',
  styleUrl: './solver-input.css',
})
export class SolverInput {
  private SimplexService = inject(Results);
  isLoading = this.SimplexService.isLoading;
  variableBounds = signal<string[]>(['>= 0', '>= 0']);
  objectiveType = signal<'MAX' | 'MIN'>('MAX');
  numVars = signal<number>(2);
  objective = signal<number[]>([0, 0]);
  constraints = signal<{ coefs: number[], sign: string, rhs: number }[]>([
    { coefs: [0, 0], sign: '<=', rhs: 0 }
  ]);
  hasObjectiveError = signal<boolean>(false);
  variableNames = computed(() =>
    Array.from({ length: this.numVars() }, (_, i) => `x${i + 1}`)
  );
  updateVarCount(change: number) {
    const newVal = this.numVars() + change;

    // Keep it between 2 and 10 variables
    if (newVal >= 2 && newVal <= 10) {
      this.numVars.set(newVal);

      // Resize the objective function array, keeping existing numbers if possible
      this.objective.update(current => {
        const arr = new Array(newVal).fill(0);
        current.forEach((val, i) => { if (i < newVal) arr[i] = val; });
        return arr;
      });

      // Resize every constraint array to match the new variable count
      this.constraints.update(list => list.map(c => {
        const newCoefs = new Array(newVal).fill(0);
        c.coefs.forEach((val, i) => { if (i < newVal) newCoefs[i] = val; });
        return { ...c, coefs: newCoefs };
      }));
      this.variableBounds.update((current) => {
        const arr = new Array(newVal).fill('>= 0');
        current.forEach((val, i) => {
          if (i < newVal) arr[i] = val;
        });
        return arr;
      });
    }
  }

  addConstraint() {
    this.constraints.update(list => [
      ...list,
      { coefs: new Array(this.numVars()).fill(0), sign: '<=', rhs: 0 }
    ]);
  }

  removeConstraint(index: number) {
    this.constraints.update(list => list.filter((_, i) => i !== index));
  }
  solve() {
    const allZeros = this.objective().every(val => val === 0);
    if (allZeros) {
      this.hasObjectiveError.set(true);
      return;
    }
    this.hasObjectiveError.set(false);
    const objStr = this.formateEquations(this.objective());
    const constraintsList = this.constraints().map(c => {
      const leftSide = this.formateEquations(c.coefs);
      return `${leftSide}${c.sign}${c.rhs}`
    });
    const boundsList = this.variableBounds().map((bound, index) => {
      return `x${index + 1} ${bound}`
    });
    const payload: BackendData = {
      objectiveType: this.objectiveType(),
      objectiveFunction: objStr,
      constraints: constraintsList,
      bounds: boundsList
    };
    console.log(payload);

    this.SimplexService.calculateSimplex(payload);
  }

  private formateEquations(coefs: number[]): string {
    let equation = '';
    coefs.forEach((coef, index) => {
      const varName = `x${index + 1}`;
      if(index !== 0){
        equation += '+';
      }
     equation += coef;
      equation += varName;
    });
    return equation.length > 0 ? equation : "0";
  }
}
