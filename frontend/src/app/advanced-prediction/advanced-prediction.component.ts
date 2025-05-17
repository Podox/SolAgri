import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PredictionService } from '../services/prediction.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-advanced-prediction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,NavbarComponent],
  templateUrl: './advanced-prediction.component.html',
  styleUrls: ['./advanced-prediction.component.css']
})
export class AdvancedPredictionComponent implements OnInit {
  form: FormGroup;
  predictionId!: number;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private predictionService: PredictionService,
    private router: Router
  ) {
    this.form = this.fb.group({
      soilType: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.queryParamMap.get('predictionId');
    if (id) {
      this.predictionId = +id;
    } else {
      this.error = 'Missing prediction ID in URL';
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.predictionId) return;

    const data = {
      predictionId: this.predictionId,
      soilType: this.form.value.soilType
    };

    this.predictionService.updatePredictionWithSoil(data).subscribe({
      next: () => this.router.navigate(['/predictions']),
      error: (err: { error: string; }) => {
        this.error = err?.error || 'Failed to update prediction';
      }
    });
  }
}
