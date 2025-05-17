// admin-support.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { SupportService } from '../../services/support.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-support',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './admin-support.component.html',
  styleUrls: ['./admin-support.component.css']
})
export class AdminSupportComponent implements OnInit {
  tickets: any[] = [];
  error: string | null = null;

  constructor(private supportService: SupportService) {}

  ngOnInit(): void {
    this.loadAllTickets();
  }

  loadAllTickets(): void {
    this.supportService.getAllTickets().subscribe({
      next: (res) => this.tickets = res,
      error: (err) => this.error = err.error || 'Access denied or failed to load tickets.'
    });
  }

  updateStatus(ticketId: number, status: string): void {
    this.supportService.updateStatus(ticketId, status).subscribe({
      next: () => this.loadAllTickets(),
      error: (err) => this.error = err.error || 'Failed to update ticket status.'
    });
  }
}
