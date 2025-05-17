import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PredictionService } from '../services/prediction.service';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-prediction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule,NavbarComponent],
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
      landSurface: ['', [Validators.required, Validators.min(100)]],
      waterDepth: ['', [Validators.required, Validators.min(0.3)]],
      waterTravelingDistance: ['', [Validators.required, Validators.min(1)]],
      soilType: [''] // Optional
    });
  }
  onSubmit(): void {
    if (this.predictionForm.invalid) return;

    this.loading = true;
    this.error = null;

    const formData = this.predictionForm.value;
    const soilType = formData.soilType;
    delete formData.soilType; // ❌ remove it from the body

    this.predictionService.createPrediction(formData, soilType).subscribe({
      next: () => {
        this.router.navigate(['/predictions']);
      },
      error: (err: { error: string }) => {
        this.error = err?.error || 'Something went wrong';
        this.loading = false;
      }
    });
  }

}
