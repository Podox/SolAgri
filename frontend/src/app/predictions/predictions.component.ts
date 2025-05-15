import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PredictionService } from '../services/prediction.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-predictions',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './predictions.component.html',
  styleUrls: ['./predictions.component.css']
})
export class PredictionsComponent implements OnInit {
  predictions: any[] = [];
  error: string | null = null;
  loading = true;

  constructor(private predictionService: PredictionService) {}

  ngOnInit(): void {
    this.predictionService.getUserPredictions().subscribe({
      next: (res) => {
        this.predictions = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error || 'Could not load predictions.';
        this.loading = false;
      }
    });
  }
}
