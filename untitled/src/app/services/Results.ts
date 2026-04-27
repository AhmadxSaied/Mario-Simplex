import { Injectable, inject, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BackendData } from "../models/SendData";
import { SimplexResults } from "../models/resultsStructure";
@Injectable({
  providedIn: 'root'
})
export class Results {
  private http = inject(HttpClient);
  public steps = signal<SimplexResults[] | null>(null);
  public isLoading = signal<boolean>(false);
  public error = signal<string | null>(null);

  calculateSimplex(payload: BackendData) {
    this.isLoading.set(true);
    this.error.set(null);
    this.steps.set(null);
    const apiUrl = 'http:localhost:8080/api/solve';
    this.http.post<SimplexResults[]>(apiUrl, payload).subscribe({
      next: (Response) => {
        this.steps.set(Response);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error("error with backend ,", err);
        this.error.set("failed");
        this.isLoading.set(false);
      }
    })
  }
}
