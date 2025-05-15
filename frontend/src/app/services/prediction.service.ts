import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createPrediction(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/predict`, data, {
      withCredentials: true
    });
  }
  updatePredictionWithSoil(data: { predictionId: number; soilType: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/advanced-predict`, data, {
      withCredentials: true
    });
  }
  getUserPredictions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/predictions`, {
      withCredentials: true
    });
  }
}
