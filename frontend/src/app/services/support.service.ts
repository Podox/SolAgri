import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SupportService {
  private apiUrl = 'http://localhost:8080/api/support';

  constructor(private http: HttpClient) {}

  getMyTickets(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`, { withCredentials: true });
  }

  createTicket(data: { subject: string; description: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, data, { withCredentials: true });
  }

  getTicket(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  getAllTickets(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin`, { withCredentials: true });
  }

  updateStatus(ticketId: number, status: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/admin/update-status?ticketId=${ticketId}&status=${status}`, {}, { withCredentials: true });
  }
}
