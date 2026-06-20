import {Component, inject, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SoolverResult } from './shared/soolver-result/soolver-result';
import { SolverInput } from './shared/solver-input/solver-input';
import {Results} from './services/Results';
import { LoadingSimplex } from './shared/loading_screens/loading-simplex/loading-simplex';
import { ExitSimplex } from './shared/loading_screens/loading-solver-to-main/exit-simplex';
import { SplashScreen } from './shared/loading_screens/SplashScreen';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SoolverResult, SolverInput, LoadingSimplex, ExitSimplex, SplashScreen],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  public simplexService = inject(Results);
  isAppStarted = false;
  isLoading = false;
  isExiting = false;
  onAppStart() {
    this.isAppStarted = true;
  }
  onLoadingFinished() {
    this.isLoading = false;
  }
  startNewProblem() {
    this.isExiting = true;
  }

  // This runs when the 3-second vertical pipe animation finishes
  onExitTransitionFinished() {
    this.isExiting = false;
    // Finally clear the math steps to reset the form behind the scenes
    this.simplexService.steps.set(null);
  }
}
