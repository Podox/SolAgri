import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../services/feedback.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NavbarComponent],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class FeedbackComponent implements OnInit {
  form: FormGroup;
  predictionId!: number;
  feedbacks: any[] = [];
  error: string | null = null;
  isSubmitting = false;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private router: Router
  ) {
    this.form = this.fb.group({
      feedbackMessage: ['', [Validators.required, Validators.minLength(10)]],
      rating: [0, [Validators.required, Validators.min(1), Validators.max(5)]]
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

  setRating(rating: number): void {
    this.form.patchValue({ rating });
  }

  loadFeedbacks(): void {
    this.feedbackService.getFeedbacks(this.predictionId).subscribe({
      next: (res) => {
        this.feedbacks = res;
        this.error = null;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load feedback';
        console.error('Error loading feedback:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid || this.isSubmitting) return;
    
    this.isSubmitting = true;
    this.error = null;

    this.feedbackService.addFeedback(this.predictionId, this.form.value).subscribe({
      next: () => {
        this.form.reset({ feedbackMessage: '', rating: 0 });
        this.loadFeedbacks();
        this.isSubmitting = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to submit feedback';
        this.isSubmitting = false;
        console.error('Error submitting feedback:', err);
      }
    });
  }
}
