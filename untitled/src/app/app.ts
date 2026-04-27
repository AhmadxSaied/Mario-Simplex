import {Component, inject, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SoolverResult } from './shared/soolver-result/soolver-result';
import { SolverInput } from './shared/solver-input/solver-input';
import {Results} from './services/Results';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SoolverResult, SolverInput],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
public simplexService = inject(Results);
}
