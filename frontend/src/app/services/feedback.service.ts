import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FeedbackService {
  private apiUrl = 'http://localhost:8080/api/feedback';

  constructor(private http: HttpClient) {}

  addFeedback(predictionId: number, data: { feedbackMessage: string; rating: number }): Observable<any> {
    return this.http.post(`${this.apiUrl}/${predictionId}`, data, {
      withCredentials: true
    });
  }

  getFeedbacks(predictionId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${predictionId}`, {
      withCredentials: true
    });
  }
}
