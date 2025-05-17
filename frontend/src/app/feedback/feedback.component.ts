import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../services/feedback.service';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {
  form: FormGroup;
  predictionId!: number;
  feedbacks: any[] = [];
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private router: Router
  ) {
    this.form = this.fb.group({
      feedbackMessage: ['', Validators.required],
      rating: [1, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.queryParamMap.get('id');
    if (id) {
      this.predictionId = +id;
      this.loadFeedbacks();
    } else {
      this.error = 'Missing prediction ID.';
    }
  }

  loadFeedbacks(): void {
    this.feedbackService.getFeedbacks(this.predictionId).subscribe({
      next: (res) => this.feedbacks = res,
      error: (err) => this.error = err.error || 'Failed to load feedback'
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.feedbackService.addFeedback(this.predictionId, this.form.value).subscribe({
      next: () => {
        this.form.reset({ feedbackMessage: '', rating: 1 });
        this.loadFeedbacks(); // Refresh list
      },
      error: (err) => this.error = err.error || 'Failed to submit feedback'
    });
  }
}
