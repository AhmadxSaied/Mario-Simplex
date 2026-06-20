import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Results } from '../../services/Results';

@Component({
  selector: 'app-soolver-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './soolver-result.html',
  styleUrl: './soolver-result.css',
})
export class SoolverResult {
  private simplexService = inject(Results);
  // 1. The full history of steps
  steps = computed(() => this.simplexService.steps() || []);
  // 2. The current position in time (starts at step 0)
  currentIndex = signal(0);

  hasResults = computed(() => this.steps().length > 0);
  // 3. A computed signal that ALWAYS holds the current step's data
  currentStep = computed(() => this.steps()[this.currentIndex()]);

  // 4. All your table variables now look at the `currentStep` instead of `results`
  variablesNames = computed(() => this.currentStep().VariableNames);
  matrix = computed(() => this.currentStep().Matrix);
  Basic_variables = computed(() => this.currentStep().Basic_Variables);
  b_row = computed(() => this.currentStep().b_row);
  ratioResults = computed(() => this.currentStep().RatioResults);
  pivotRow = computed(() => this.currentStep().PivotRow);
  pivotCol = computed(() => this.currentStep().PivotCol);
  z_row = computed(() => this.currentStep().z_row);
  phase = computed(() => this.currentStep().Type);
  isPivot(rowIndex: number, colIndex: number): boolean {
    return rowIndex === this.pivotRow() && colIndex === this.pivotCol();
  }

  // --- NAVIGATION CONTROLS ---

  nextStep() {
    if (this.currentIndex() < this.steps().length - 1) {
      this.currentIndex.update((index) => index + 1);
    }
  }

  prevStep() {
    if (this.currentIndex() > 0) {
      this.currentIndex.update((index) => index - 1);
    }
  }

  progressPercentage = computed(() => {
    const totalSteps = this.steps().length;

    // Safety check: If there are no steps, return 0%
    if (totalSteps === 0) return 0;

    // Safety check: If there is only 1 step total, it's instantly 100% done
    if (totalSteps === 1) return 100;

    return (this.currentIndex() / (totalSteps - 1)) * 100;
  });
  isInVar(colIndex: number) {
    return colIndex === this.pivotCol();
  }
}
