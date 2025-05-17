import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PredictionService } from '../services/prediction.service';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

interface Prediction {
  id: number;
  seed: string;
  landSurface: number;
  waterDepth: number;
  waterTravelingDistance: number;
  kilowatts: number;
  panels: number;
  surfaceArea: number;
  cost: number;
  explanation: string;
  advancedPrediction?: {
    soilType: string;
  };
}

@Component({
  selector: 'app-predictions',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './predictions.component.html',
  styleUrls: ['./predictions.component.css']
})
export class PredictionsComponent implements OnInit {
  predictions: Prediction[] = [];
  error: string | null = null;
  loading = true;

  constructor(private predictionService: PredictionService) {}

  ngOnInit(): void {
    this.predictionService.getUserPredictions().subscribe({
      next: (res: Prediction[]) => {
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
