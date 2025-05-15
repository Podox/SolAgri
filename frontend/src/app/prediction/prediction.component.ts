import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PredictionService } from '../services/prediction.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-prediction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './prediction.component.html',
  styleUrls: ['./prediction.component.css']
})
export class PredictionComponent {
  predictionForm: FormGroup;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private predictionService: PredictionService,
    private router: Router
  ) {
    this.predictionForm = this.fb.group({
      seed: ['', Validators.required],
      landSurface: ['', [Validators.required, Validators.min(0.1)]],
      waterDepth: ['', [Validators.required, Validators.min(0.1)]],
      waterTravelingDistance: ['', [Validators.required, Validators.min(0.1)]],
      soilType: [''] // Optional
    });
  }

  onSubmit(): void {
    if (this.predictionForm.invalid) return;

    this.loading = true;
    this.error = null;

    this.predictionService.createPrediction(this.predictionForm.value).subscribe({
      next: () => {
        this.router.navigate(['/predictions']);
      },
      error: (err: { error: string; }) => {
        this.error = err?.error || 'Something went wrong';
        this.loading = false;
      }
    });
  }
}
